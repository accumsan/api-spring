package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import com.minhdd.app.ml.service.kaggle.CsvUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 * http://spark.apache.org/docs/latest/ml-classification-regression.html#logistic-regression
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class SantanderCustomerSatisfactionBinaryClassification extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(SantanderCustomerSatisfactionBinaryClassification.class);

    @Override
    public MLService loadData() {
        JavaRDD<LabeledPoint> train = CsvUtil.getLabeledPointJavaRDDFromKaggleCsv(trainPath, sqlContext, 2, "TARGET");
        JavaRDD<LabeledPoint> validation = CsvUtil.getLabeledPointJavaRDDFromKaggleCsv(validationPath, sqlContext, 2, "TARGET");
        JavaRDD<LabeledPoint> test = CsvUtil.getLabeledPointJavaRDDFromKaggleCsv(testPath, sqlContext, 2, "TARGET");
        return super.loadData(null, train, validation, test);
    }

    @Override
    public MLService loadInput(String inputPath) {
        JavaRDD<LabeledPoint> input = CsvUtil.getLabeledPointJavaRDDFromKaggleCsv(inputPath, sqlContext, 1, "ID");
        return super.setInput(input);
    }

    @Override
    protected MLAlgorithm<LogisticRegressionModel, JavaRDD<LabeledPoint>> algorithm() {
        final LogisticRegressionWithLBFGS model = new LogisticRegressionWithLBFGS().setNumClasses(2);
        LogisticRegressionModel lrm = model.run(((JavaRDD<LabeledPoint>) dataSet.getTraining()).rdd());
        lrm.clearThreshold();

        // Compute raw scores on the test set.
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels = ((JavaRDD<LabeledPoint>) dataSet.getCrossValidation()).map(p -> {
            Double prediction = lrm.predict(p.features());
            return new Tuple2<>(prediction, p.label());
        });
        BinaryClassificationMetrics metrics = new BinaryClassificationMetrics(predictionAndLabels.rdd());
        JavaRDD<Tuple2<Object, Object>> f1Score = metrics.fMeasureByThreshold().toJavaRDD();
        Tuple2<Object, Object> f1scoreMax = f1Score.reduce((a, b) ->
                ((Double) a._2() - (Double) b._2() > 0) ? a : b
        );
        System.out.println("F1 Score max: " + f1scoreMax);
        lrm.setThreshold((Double) f1scoreMax._1());
        return (JavaRDD<LabeledPoint> training) -> lrm;
    }

    @Override
    public MLService test() {
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels = getTuple2PredictionAndLabels((JavaRDD<LabeledPoint>) dataSet.getCrossValidation());
        predictions = predictionAndLabels;
        return super.test();
    }

    private JavaRDD<Tuple2<Object, Object>> getTuple2PredictionAndLabels(JavaRDD<LabeledPoint> data) {
        LogisticRegressionModel lrm = (LogisticRegressionModel) model;
        return data.map(p -> {
            Double prediction = lrm.predict(p.features());
            return new Tuple2<>(prediction, p.label());
        });
    }


    @Override
    public Map<String, Object> getResults() {
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels = (JavaRDD<Tuple2<Object, Object>>) predictions;
        System.out.println("Number of predictions : " + predictionAndLabels.count());
        System.out.println("Number of label 1 : " + predictionAndLabels.filter(t -> t._2().equals(1.0)).count());
        System.out.println("Number of predicted 1 : " + predictionAndLabels.filter(t -> t._1().equals(1.0)).count());
        long truePositive = predictionAndLabels.filter(t -> t._1().equals(1.0) && t._2().equals(1.0)).count();
        System.out.println("Number of Good predictions for label 1 :" + truePositive);
        long falsePositive = predictionAndLabels.filter(t -> t._1().equals(1.0) && t._2().equals(0.0)).count();
        System.out.println("Number of Bad predictions (to 1) of label 0 :" + falsePositive);
        long falseNegative = predictionAndLabels.filter(t -> t._1().equals(0.0) && t._2().equals(1.0)).count();
        System.out.println("Number of Bad predictions (to 0) of label 1 :" + falseNegative);
        double precision = (double) truePositive / (truePositive + falsePositive);
        double recall = (double) truePositive / (truePositive + falseNegative);
        double fscore = 2 * precision * recall / (precision + recall);
        System.out.println("precision :" + precision);
        System.out.println("recall :" + recall);
        System.out.println("fscore :" + fscore);
        return new HashMap<>();
    }

    @Override
    public void save(String modelFilePath) {
        ((LogisticRegressionModel) model).save(sparkContext, modelFilePath);
    }

    @Override
    public void restore(String modelFilePath) {
        model = LogisticRegressionModel.load(sparkContext, modelFilePath);
    }

    @Override
    public void produce(String output) {
        JavaRDD<Tuple2<Object, Object>> predictions = getTuple2PredictionAndLabels((JavaRDD<LabeledPoint>) dataSet.getInput());
        JavaRDD<Row> modifiedRDD = predictions.map(t -> {
            double target = ((Double) t._1()).doubleValue() ;
            double id = ((Double) t._2()).doubleValue();
            return RowFactory.create((int) id, (int) target);
        });
        StructType schema = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("ID", DataTypes.IntegerType, true),
                DataTypes.createStructField("TARGET", DataTypes.IntegerType, true)));
        DataFrame results = sqlContext.createDataFrame(modifiedRDD, schema);
        CsvUtil.save(results, output, true);
    }

}
