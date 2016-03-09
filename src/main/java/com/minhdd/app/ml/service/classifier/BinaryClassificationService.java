package com.minhdd.app.ml.service.classifier;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 * http://spark.apache.org/docs/latest/ml-classification-regression.html#logistic-regression
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class BinaryClassificationService extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(BinaryClassificationService.class);

    @Override
    public MLService loadData() {
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sparkContext, filePath).toJavaRDD();
        double f = 0;
        if (conf != null) {
            f = conf.getFractionTest();
        }
        if (f > 0) {
            JavaRDD<LabeledPoint>[] splits =
                    data.randomSplit(new double[]{1 - f, f}, 11L);
            JavaRDD<LabeledPoint> training = splits[0].cache();
            JavaRDD<LabeledPoint> test = splits[1];
            return super.loadData(data, training, null, test);
        }
        return super.loadData(data);
    }

    @Override
    protected MLAlgorithm<LogisticRegressionModel, JavaRDD<LabeledPoint>> algorithm() {
        final LogisticRegressionWithLBFGS model = new LogisticRegressionWithLBFGS().setNumClasses(2);
        return (JavaRDD<LabeledPoint> training) -> model.run(training.rdd());
    }

    @Override
    public MLService test() {
        LogisticRegressionModel lrm = (LogisticRegressionModel) model;
        lrm.clearThreshold();

        // Compute raw scores on the test set.
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels = ((JavaRDD<LabeledPoint>) dataSet.getTest()).map(
                (Function<LabeledPoint, Tuple2<Object, Object>>) p -> {
                    Double prediction = lrm.predict(p.features());
                    return new Tuple2<Object, Object>(prediction, p.label());
                }
        );
        predictions = predictionAndLabels;
        return super.test();
    }

    @Override
    public Map<String, Object> getResults() {
        JavaRDD<Tuple2<Object, Object>> predictions = (JavaRDD<Tuple2<Object, Object>>) this.predictions;

        // Get evaluation metrics.
        BinaryClassificationMetrics metrics = new BinaryClassificationMetrics(predictions.rdd());

        // Precision by threshold
        JavaRDD<Tuple2<Object, Object>> precision = metrics.precisionByThreshold().toJavaRDD();
        System.out.println("Precision by threshold: " + precision.collect());
        System.out.println("Precision max: " + precision.reduce((a, b) ->
                ((Double) a._2() - (Double) b._2() > 0) ? a : b
        ));

        // Recall by threshold
        JavaRDD<Tuple2<Object, Object>> recall = metrics.recallByThreshold().toJavaRDD();
        System.out.println("Recall by threshold: " + recall.collect());
        System.out.println("Recall max: " + recall.reduce((a, b) ->
                ((Double) a._2() - (Double) b._2() > 0) ? a : b
        ));

        // F Score by threshold
        JavaRDD<Tuple2<Object, Object>> f1Score = metrics.fMeasureByThreshold().toJavaRDD();
        System.out.println("F1 Score by threshold: " + f1Score.collect());
        System.out.println("F1 Score max: " + f1Score.reduce((a, b) ->
                ((Double) a._2() - (Double) b._2() > 0) ? a : b
        ));

        JavaRDD<Tuple2<Object, Object>> f2Score = metrics.fMeasureByThreshold(2.0).toJavaRDD();
        System.out.println("F2 Score by threshold: " + f2Score.collect());
        System.out.println("F2 Score max: " + precision.reduce((a, b) ->
                ((Double) a._2() - (Double) b._2() > 0) ? a : b
        ));

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

        Map<String, Object> responses = new HashMap<>();
        return responses;
    }

    @Override
    public void save(String modelFilePath) {
        ((LogisticRegressionModel) model).save(sparkContext, modelFilePath);
    }

    @Override
    public void restore(String modelFilePath) {
        LogisticRegressionModel.load(sparkContext, modelFilePath);
    }

}
