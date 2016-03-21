package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import com.minhdd.app.ml.outil.CsvUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.classification.*;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.types.DataTypes;
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
public class SantanderCustomerSatisfactionRegression extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(SantanderCustomerSatisfactionRegression.class);
    boolean scale = false;

    @Override
    public MLService loadData() {
        DataFrame data = ScsUtil.getDataFrameFromCsv(trainPath, sqlContext, scale).select("ID", "features", "TARGET");
        DataFrame train = data.withColumn("label", data.col("TARGET").cast(DataTypes.DoubleType));
        DataFrame test = ScsUtil.getDataFrameFromCsv(testPath, sqlContext, scale).select("ID", "features", "TARGET");
        test = test.withColumn("label", test.col("TARGET").cast(DataTypes.DoubleType));
        return super.loadData(train, train, null, test);
    }

    @Override
    public MLService loadInput(String inputPath) {
        DataFrame data = ScsUtil.getDataFrameFromCsv(inputPath, sqlContext, scale).select("ID", "features");
        return super.setInput(data);
    }

    @Override
    protected MLAlgorithm<LogisticRegressionModel, DataFrame> algorithm() {
        LogisticRegression lr = new LogisticRegression()
                .setMaxIter(conf.getMaxIteration())
                .setRegParam(conf.getRegParam())
                .setElasticNetParam(conf.getElasticNetParam());
        return (DataFrame training) -> lr.fit(training);
    }

    @Override
    public MLService test() {
        //predictions = ((PipelineModel) model).transform((DataFrame) dataSet.getCrossValidation());
        return super.test();
    }

    @Override
    public Map<String, Object> getResults() {
        logisticRegressionResults((LogisticRegressionModel) model);
        return null;
    }

    private void logisticRegressionResults(LogisticRegressionModel lrModel) {
        System.out.println("================================================");
        LogisticRegressionTrainingSummary trainingSummary = lrModel.summary();
//        double[] objectiveHistory = trainingSummary.objectiveHistory();
//        for (double lossPerIteration : objectiveHistory) {
//            System.out.println(lossPerIteration);
//        }
        BinaryLogisticRegressionSummary binarySummary = (BinaryLogisticRegressionSummary) trainingSummary;
//        DataFrame roc = binarySummary.roc();
//        roc.show();
//        roc.select("FPR").show();
//        System.out.println(binarySummary.areaUnderROC());

        DataFrame fMeasure = binarySummary.fMeasureByThreshold();
        double maxFMeasure = fMeasure.select(max("F-Measure")).head().getDouble(0);
        double bestThreshold = fMeasure.where(fMeasure.col("F-Measure").equalTo(maxFMeasure)).select("threshold").head().getDouble(0);
        lrModel.setThreshold(bestThreshold);
        System.out.println("================================================");
        DataFrame predictions = lrModel.transform((DataFrame) dataSet.getTest());
        System.out.println("Number of predictions : " + predictions.count());
        System.out.println("Number of label 1 : " + predictions.filter("TARGET = 1").count());
        System.out.println("Number of predicted 1 : " + predictions.filter("prediction = 1.0").count());
        long truePositive = predictions.filter("TARGET = 1").filter("prediction = 1.0").count();
        System.out.println("Good predictions for label 1 : " + truePositive);
        long falsePositive = predictions.filter("TARGET = 0").filter("prediction = 1.0").count();
        long falseNegative = predictions.filter("TARGET = 1").filter("prediction = 0.0").count();
        System.out.println("Bad predictions (to 1) of target 0 : " + falsePositive);
        double precision = (double) truePositive / (truePositive + falsePositive);
        double recall = (double) truePositive / (truePositive + falseNegative);
        double fscore = 2 * precision * recall / (precision + recall);
        System.out.println("precision :" + precision);
        System.out.println("recall :" + recall);
        System.out.println("fscore :" + fscore);
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels =
                predictions.select("probability", "TARGET").toJavaRDD().map(a -> {
                    double score = ((DenseVector) a.get(0)).apply(1);
                    return new Tuple2<>(score, ((Integer)a.get(1)).doubleValue());
                });
        BinaryClassificationMetrics metrics = new BinaryClassificationMetrics(predictionAndLabels.rdd());
        JavaRDD<Tuple2<Object, Object>> f1Score = metrics.fMeasureByThreshold().toJavaRDD();
        System.out.println("F1 Score max: " + f1Score.reduce((a, b) ->
                ((Double) a._2() - (Double) b._2() > 0) ? a : b
        ));
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
}
