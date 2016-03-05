package com.minhdd.app.ml.example;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.*;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 * http://spark.apache.org/docs/latest/ml-classification-regression.html#linear-regression
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class DecisionTreeClassifierService extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(DecisionTreeClassifierService.class);

    @Inject
    private SQLContext sqlContext;

    @Override
    public MLService loadData() {
        DataFrame data = loadFile(sqlContext);
        DataFrame[] splits = data.randomSplit(new double[]{0.7, 0.3});
        DataFrame trainingData = splits[0];
        DataFrame testData = splits[1];
        return super.loadData(data, trainingData, null, testData);
    }

    @Override
    protected MLAlgorithm<PipelineModel> algorithm() {
        StringIndexerModel labelIndexer = new StringIndexer()
                .setInputCol("label")
                .setOutputCol("indexedLabel")
                .fit(dataSet.getData());

        VectorIndexerModel featureIndexer = new VectorIndexer()
                .setInputCol("features")
                .setOutputCol("indexedFeatures")
                .setMaxCategories(4) // features with > 4 distinct values are treated as continuous
                .fit(dataSet.getData());

        DecisionTreeClassifier dt = new DecisionTreeClassifier()
                .setLabelCol("indexedLabel")
                .setFeaturesCol("indexedFeatures");

        IndexToString labelConverter = new IndexToString()
                .setInputCol("prediction")
                .setOutputCol("predictedLabel")
                .setLabels(labelIndexer.labels());

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{labelIndexer, featureIndexer, dt, labelConverter});

        return (DataFrame training) -> pipeline.fit(training);
    }

    @Override
    protected DataFrame transform(DataFrame test) {
        return ((PipelineModel) model).transform(test);
    }

    @Override
    public Map<String, Object> getResults() {
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("indexedLabel")
                .setPredictionCol("prediction")
                .setMetricName("precision");
        double accuracy = evaluator.evaluate(predictions);
        logger.info("Test Error = " + (1.0 - accuracy));

        DecisionTreeClassificationModel treeModel =
                (DecisionTreeClassificationModel) (((PipelineModel) model).stages()[2]);
        logger.info("Learned classification tree model:\n" + treeModel.toDebugString());

        Map<String, Object> responses = new HashMap<>();
        responses.put("error", 1.0 - accuracy);
        return responses;
    }
}
