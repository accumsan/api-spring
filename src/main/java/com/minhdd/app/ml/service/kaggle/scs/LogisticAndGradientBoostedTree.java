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
        DataFrame validation = CsvUtil.loadCsvFile(sqlContext, validationPath, true, true);
        DataFrame test = ScsUtil.getDataFrameFromCsv(testPath, sqlContext).select("TARGET", "features", "ID");

        final LogisticRegressionWithLBFGS model = new LogisticRegressionWithLBFGS().setNumClasses(2);
        LogisticRegressionModel lrm = model.run(trainRDD.rdd());
        lrm.clearThreshold();
        JavaRDD<Tuple2<Object, Object>> predictionAndID = validationFeaturesAndID.map(p -> new Tuple2<>(lrm.predict(p.features()), p.label()));
        DataFrame predictionAndIDDataFrame = DataFrameUtil.doublesFromJavaRDD(sqlContext, predictionAndID, "logistic_prediction", "ID");

        DataFrame validationAddedWithLogisticPrediction = predictionAndIDDataFrame.join(validation, "ID");
        DataFrame validationInput = DataFrameUtil.assembled(validationAddedWithLogisticPrediction, "features");

        GBTClassifier classifier = new GBTClassifier()
                .setLabelCol("indexedLabel")
                .setFeaturesCol("indexedFeatures")
                .setMaxIter(conf.getMaxIteration());
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
        PipelineModel pipelineModel = pipeline.fit(validationInput);
        predictions = pipelineModel.transform(test);
        return training -> null;
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

        JavaRDD<Tuple2<Object, Object>> predictionAndLabels =
                predictions.select("prediction", "indexedLabel").toJavaRDD()
                        .map(a -> new Tuple2<>(a.get(0), a.get(1)));
        BinaryClassificationMetrics metrics = new BinaryClassificationMetrics(predictionAndLabels.rdd());
        System.out.println("Area under ROC = " + metrics.areaUnderROC());
        return null;
    }
}
