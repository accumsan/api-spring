package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.*;
import com.minhdd.app.ml.outil.CsvUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.*;
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
import java.util.Map;

import static org.apache.spark.sql.functions.max;

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
        DataFrame train = ScsUtil.getDataFrameFromCsv(trainPath, sqlContext, false).select("ID", "features", "TARGET");
        DataFrame validation = ScsUtil.getDataFrameFromCsv(validationPath, sqlContext, false).select("ID", "features", "TARGET");
        DataFrame test = ScsUtil.getDataFrameFromCsv(testPath, sqlContext, false).select("ID", "features", "TARGET");
        return super.loadData(train, train, validation, test);
    }

    @Override
    public MLService loadInput(String inputPath) {
        DataFrame data = ScsUtil.getDataFrameFromCsv(inputPath, sqlContext, false).select("ID", "features");
        return super.setInput(data);
    }

    @Override
    protected MLAlgorithm<PipelineModel, DataFrame> algorithm() {
        Object classifier = null;
        if (conf != null) {
            if (MLEnum.GradientBoostedTree.equals(conf.getAlgorithm())) {
                classifier = new GBTClassifier()
                        .setLabelCol("indexedLabel")
                        .setFeaturesCol("indexedFeatures")
                        .setMaxIter(conf.getMaxIteration());
            } else if (MLEnum.RandomForest.equals(conf.getAlgorithm())) {
                classifier = new RandomForestClassifier()
                        .setLabelCol("indexedLabel")
                        .setFeaturesCol("indexedFeatures");
            }
        }

        StringIndexerModel labelIndexer = new StringIndexer()
                .setInputCol("TARGET")
                .setOutputCol("indexedLabel")
                .fit((DataFrame) dataSet.getTraining());

        VectorIndexerModel featureIndexer = new VectorIndexer()
                .setInputCol("features")
                .setOutputCol("indexedFeatures")
                .setMaxCategories(3) // features with > 3 distinct values are treated as continuous
                .fit((DataFrame) dataSet.getTraining());

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{labelIndexer, featureIndexer, (PipelineStage) classifier});

        return (DataFrame training) -> pipeline.fit(training);
    }

    @Override
    public MLService test() {
        predictions = ((PipelineModel) model).transform((DataFrame) dataSet.getCrossValidation());
        return super.test();
    }

    @Override
    public Map<String, Object> getResults() {
        DataFrame predictions = (DataFrame) this.predictions;
        DataFrame predictionsToShow = predictions.select("ID", "TARGET", "prediction");
        System.out.println("================================================");
        System.out.println("Number of predictions : " + predictionsToShow.count());
        System.out.println("Number of target 1 : " + predictionsToShow.filter("TARGET = 1").count());
        System.out.println("Number of predicted 1 : " + predictionsToShow.filter("prediction = 1.0").count());
        System.out.println("Good predictions for target 1 : " +
        predictionsToShow.filter("TARGET = 1").filter("prediction = 1.0").count());
        System.out.println("Bad predictions (to 1) of target 0 : " +
        predictionsToShow.filter("TARGET = 0").filter("prediction = 1.0").count());
        System.out.println("Bad predictions (to 0) of target 1 : " +
        predictionsToShow.filter("TARGET = 1").filter("prediction = 0.0").count());
        if (MLEnum.GradientBoostedTree.equals(conf.getAlgorithm())) {
            printGBTResults(predictions);
        } else if (MLEnum.RandomForest.equals(conf.getAlgorithm())) {
            printRFCResults(predictions);
        }
        return null;
    }

    public void printGBTResults(DataFrame predictions) {
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels =
                predictions.select("prediction", "indexedLabel").toJavaRDD()
                        .map(a -> new Tuple2<>(a.get(0), a.get(1)));
        printMetrics(predictionAndLabels);
    }

    public void printRFCResults(DataFrame predictions) {
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels =
                predictions.select("probability", "indexedLabel").toJavaRDD().map(a -> {
                    double score = ((DenseVector) a.get(0)).apply(0);
                    return new Tuple2<>(score, a.get(1));
                });
        printMetrics(predictionAndLabels);
    }

    private void printMetrics(JavaRDD<Tuple2<Object, Object>> predictionAndLabels) {
        BinaryClassificationMetrics metrics = new BinaryClassificationMetrics(predictionAndLabels.rdd());
        System.out.println("Area under ROC = " + metrics.areaUnderROC());
        // Precision by threshold
//        JavaRDD<Tuple2<Object, Object>> precision = metrics.precisionByThreshold().toJavaRDD();
        System.out.println();
        //System.out.println("Precision by threshold: \t" + precision.collect());

        // Recall by threshold
//        JavaRDD<Tuple2<Object, Object>> recall = metrics.recallByThreshold().toJavaRDD();
        //System.out.println("Recall by threshold: \t\t" + recall.collect());

        // F Score by threshold
        JavaRDD<Tuple2<Object, Object>> f1Score = metrics.fMeasureByThreshold().toJavaRDD();
               // .filter((Tuple2<Object, Object> a) -> (double) a._2() > 0.09);
        System.out.println("F1 Score by threshold: \t\t" + f1Score.collect());
        System.out.println("F1 Score max: " + f1Score.reduce((a, b) ->
                ((Double) a._2() - (Double) b._2() > 0) ? a : b
        ));
//        JavaRDD<Tuple2<Object, Object>> f2Score = metrics.fMeasureByThreshold(2.0).toJavaRDD()
//                .filter((Tuple2<Object, Object> a) -> (double) a._2() > 0.2);
//        System.out.println("F2 Score by threshold: \t\t" + f2Score.collect());

        // Precision-recall curve
//        JavaRDD<Tuple2<Object, Object>> prc = metrics.pr().toJavaRDD();
        //System.out.println("Precision-recall curve: \t" + prc.collect());

//        JavaRDD<Double> thresholds = precision.map(
//                (Function<Tuple2<Object, Object>, Double>) t -> new Double(t._1().toString())
//        );
//        JavaRDD<Tuple2<Object, Object>> roc = metrics.roc().toJavaRDD();
//        System.out.println("ROC curve: " + roc.collect());

        //System.out.println("Area under precision-recall curve = " + metrics.areaUnderPR());
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
                .withColumn("TARGET", predictions.col("label"))
                .select("ID", "TARGET");
        CsvUtil.save(results, output, true);
    }
}
