package com.minhdd.app.ml.service.classifier;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.classification.RandomForestClassifier;
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
 * http://spark.apache.org/docs/latest/ml-classification-regression.html#random-forest-classifier
 */
public class RandomForestClassifierService extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(RandomForestClassifierService.class);

    @Override
    public MLService loadData() {
        return loadFile(0.3, 0);
    }

    @Override
    protected MLAlgorithm<PipelineModel, DataFrame> algorithm() {
        StringIndexerModel labelIndexer = new StringIndexer()
                .setInputCol("label")
                .setOutputCol("indexedLabel")
                .fit((DataFrame)dataSet.getData());

        VectorIndexerModel featureIndexer = new VectorIndexer()
                .setInputCol("features")
                .setOutputCol("indexedFeatures")
                .setMaxCategories(4) // features with > 4 distinct values are treated as continuous
                .fit((DataFrame)dataSet.getData());

        RandomForestClassifier rf = new RandomForestClassifier()
                .setLabelCol("indexedLabel")
                .setFeaturesCol("indexedFeatures");

        IndexToString labelConverter = new IndexToString()
                .setInputCol("prediction")
                .setOutputCol("predictedLabel")
                .setLabels(labelIndexer.labels());

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{labelIndexer, featureIndexer, rf, labelConverter});

        return (DataFrame training) -> pipeline.fit(training);
    }

    @Override
    public MLService test() {
        predictions = ((PipelineModel) model).transform((DataFrame)dataSet.getCrossValidation());
        return super.test();
    }

    @Override
    public Map<String, Object> getResults() {
        DataFrame predictions = (DataFrame) this.predictions;
        predictions.select("predictedLabel", "label", "features").show(5);
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("indexedLabel")
                .setPredictionCol("prediction")
                .setMetricName("precision");
        double accuracy = evaluator.evaluate(predictions);
        logger.info("Test Error = " + (1.0 - accuracy));

        RandomForestClassificationModel rfModel = (RandomForestClassificationModel)(((PipelineModel) model).stages()[2]);
        logger.info("Learned classification forest model:\n" + rfModel.toDebugString());

        Map<String, Object> responses = new HashMap<>();
        responses.put("error", 1.0 - accuracy);
        return responses;
    }
}
