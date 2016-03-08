package com.minhdd.app.ml.service.kaggle;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.GBTClassifier;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.feature.*;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.sql.DataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 * http://spark.apache.org/docs/latest/ml-classification-regression.html#random-forest-classifier
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class SantanderCustomerSatisfaction extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(SantanderCustomerSatisfaction.class);

    @Override
    public MLService loadData() {
        DataFrame data = CsvUtil.getDataFrameFromKaggleCsv(filePath, sqlContext, 2).select("ID", "features", "TARGET");
        double f = 0;
        if (conf != null) {
            f = conf.getFractionTest();
        }
        if (f > 0) {
            DataFrame[] splits = data.randomSplit(new double[]{1 - f, f});
            DataFrame trainingData = splits[0];
            DataFrame testData = splits[1];
            return super.loadData(data, trainingData, null, testData);
        } else {
            return super.loadData(data);
        }
    }

    @Override
    public MLService loadTest() {
        DataFrame data = CsvUtil.getDataFrameFromKaggleCsv(filePath, sqlContext, 1).select("ID", "features");
        return super.setTest(data);
    }

    @Override
    protected MLAlgorithm<PipelineModel, DataFrame> algorithm() {
        StringIndexerModel labelIndexer = new StringIndexer()
                .setInputCol("TARGET")
                .setOutputCol("indexedLabel")
                .fit((DataFrame) dataSet.getData());

        VectorIndexerModel featureIndexer = new VectorIndexer()
                .setInputCol("features")
                .setOutputCol("indexedFeatures")
                .setMaxCategories(3) // features with > 3 distinct values are treated as continuous
                .fit((DataFrame) dataSet.getData());

        Object classifier = new RandomForestClassifier()
                .setLabelCol("indexedLabel")
                .setFeaturesCol("indexedFeatures");
        if ((conf != null) && (MLConfiguration.GradientBoostedTree.equals(conf.getAlgorithm()))) {
            classifier = new GBTClassifier()
                    .setLabelCol("indexedLabel")
                    .setFeaturesCol("indexedFeatures")
                    .setMaxIter(conf.getMaxIteration());
        }

        IndexToString labelConverter = new IndexToString()
                .setInputCol("prediction")
                .setOutputCol("predictedLabel")
                .setLabels(labelIndexer.labels());

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{labelIndexer, featureIndexer, (PipelineStage) classifier, labelConverter});

        return (DataFrame training) -> pipeline.fit(training);
    }

    @Override
    public MLService test() {
        predictions = ((PipelineModel) model).transform((DataFrame) dataSet.getTest());
        return super.test();
    }

    @Override
    public Map<String, Object> getResults() {
        DataFrame predictions = (DataFrame) this.predictions;
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels =
                predictions.select("probability", "indexedLabel").toJavaRDD().map(a -> {
                    double score = ((DenseVector) a.get(0)).apply(0);
                    return new Tuple2<>(score, a.get(1));
                });
        BinaryClassificationMetrics metrics = new BinaryClassificationMetrics(predictionAndLabels.rdd());
        // Precision by threshold
        JavaRDD<Tuple2<Object, Object>> precision = metrics.precisionByThreshold().toJavaRDD();
        System.out.println("Precision by threshold: " + precision.collect());

        // Recall by threshold
        JavaRDD<Tuple2<Object, Object>> recall = metrics.recallByThreshold().toJavaRDD();
        System.out.println("Recall by threshold: " + recall.collect());

        // F Score by threshold
        JavaRDD<Tuple2<Object, Object>> f1Score = metrics.fMeasureByThreshold().toJavaRDD();
        System.out.println("F1 Score by threshold: " + f1Score.collect());

        JavaRDD<Tuple2<Object, Object>> f2Score = metrics.fMeasureByThreshold(2.0).toJavaRDD();
        System.out.println("F2 Score by threshold: " + f2Score.collect());

        // Precision-recall curve
        JavaRDD<Tuple2<Object, Object>> prc = metrics.pr().toJavaRDD();
        System.out.println("Precision-recall curve: " + prc.collect());

        // Thresholds
        JavaRDD<Double> thresholds = precision.map(
                (Function<Tuple2<Object, Object>, Double>) t -> new Double(t._1().toString())
        );
        // ROC Curve
        JavaRDD<Tuple2<Object, Object>> roc = metrics.roc().toJavaRDD();
        System.out.println("ROC curve: " + roc.collect());

        // AUPRC
        System.out.println("Area under precision-recall curve = " + metrics.areaUnderPR());

        // AUROC
        System.out.println("Area under ROC = " + metrics.areaUnderROC());
//        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
//                .setLabelCol("indexedLabel")
//                .setPredictionCol("prediction")
//                .setMetricName("precision");
//        double accuracy = evaluator.evaluate(predictions);
//        //TODO another metric to exploit results other than accuracy
//        System.out.println("Accuracy = " + (accuracy * 100));
        Map<String, Object> responses = new HashMap<>();
//        responses.put("accuracy", accuracy);
        return responses;
    }

    @Override
    public void save(String modelFilePath) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFilePath));
            oos.writeObject(model);
            oos.close();
        } catch (IOException e) {
            System.out.println("Error : Maybe file do not exist :" + modelFilePath);
        }
    }

    @Override
    public void restore(String modelFilePath) {
        try {
            FileInputStream fin = new FileInputStream(modelFilePath);
            ObjectInputStream ois = new ObjectInputStream(fin);
            model = (PipelineModel) ois.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("Error ClassNotFoundException : PipelineModel");
        } catch (IOException e) {
            System.out.println("Error : Maybe file do not exist :" + modelFilePath);
        }
    }

    @Override
    public void produce(String output) {
        DataFrame predictions = (DataFrame) this.predictions;
        DataFrame results = predictions
                .withColumn("TARGET", predictions.col("predictedLabel"))
                .select("ID", "TARGET");
        CsvUtil.save(results, output, true);
    }
}
