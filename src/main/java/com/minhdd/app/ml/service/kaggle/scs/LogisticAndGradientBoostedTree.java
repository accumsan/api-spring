package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import com.minhdd.app.ml.outil.CsvUtil;
import com.minhdd.app.ml.outil.DataFrameUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.GBTClassifier;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorIndexer;
import org.apache.spark.ml.feature.VectorIndexerModel;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.DataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.util.Map;

/**
 * Created by minhdao on 21/03/16.
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class LogisticAndGradientBoostedTree extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(LogisticAndGradientBoostedTree.class);

    @Override
    public MLService loadData() {
        return super.loadData(null);
    }

    @Override
    protected MLAlgorithm algorithm() {
        JavaRDD<LabeledPoint> trainRDD = ScsUtil.getLabeledPointJavaRDDFromCsv(trainPath, sqlContext, "TARGET");
        JavaRDD<LabeledPoint> validationFeaturesAndID = ScsUtil.getLabeledPointJavaRDDFromCsv(validationPath, sqlContext, "ID");

        DataFrame validation = ScsUtil.getDataFrameFromCsv(sqlContext, validationPath, "features_0").select("ID" ,"features_0", "TARGET");
        DataFrame test = ScsUtil.getDataFrameFromCsv(sqlContext, testPath, "features_0").select("ID" ,"features_0", "TARGET");
        JavaRDD<LabeledPoint> testFeaturesAndID = ScsUtil.getLabeledPointJavaRDDFromCsv(testPath, sqlContext, "ID");
        final LogisticRegressionWithLBFGS model = new LogisticRegressionWithLBFGS().setNumClasses(2);
        LogisticRegressionModel lrm = model.run(trainRDD.rdd());
        lrm.clearThreshold();
        DataFrame validationInput = DataFrameUtil.withFeatureLogisticScores(sqlContext, validationFeaturesAndID, validation, lrm);
        DataFrame testInput = DataFrameUtil.withFeatureLogisticScores(sqlContext, testFeaturesAndID, test, lrm);
        GBTClassifier classifier = new GBTClassifier()
                .setLabelCol("indexedLabel")
                .setFeaturesCol("indexedFeatures")
                .setMaxIter(conf.getMaxIteration());
        StringIndexerModel labelIndexer = new StringIndexer()
                .setInputCol("TARGET")
                .setOutputCol("indexedLabel")
                .fit(validationInput);
        VectorIndexerModel featureIndexer = new VectorIndexer()
                .setInputCol("features")
                .setOutputCol("indexedFeatures")
                .setMaxCategories(3) // features with > 3 distinct values are treated as continuous
                .fit(validationInput);
        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{labelIndexer, featureIndexer, classifier});
        PipelineModel pipelineModel = pipeline.fit(validationInput);
        predictions = pipelineModel.transform(testInput);
        return null;
    }



    @Override
    public Map<String, Object> getResults() {
        DataFrame predictions = (DataFrame) this.predictions;
        DataFrame predictionsToShow = predictions.select("ID", "TARGET", "prediction");
        predictionsToShow.show(false);
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
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels =
            predictions.select("prediction", "indexedLabel").toJavaRDD().map(a -> new Tuple2<>(a.get(0), a.get(1)));
        BinaryClassificationMetrics metrics = new BinaryClassificationMetrics(predictionAndLabels.rdd());
        System.out.println("Area under ROC = " + metrics.areaUnderROC());
        return null;
    }
}
