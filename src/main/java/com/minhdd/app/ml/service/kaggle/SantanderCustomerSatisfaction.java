package com.minhdd.app.ml.service.kaggle;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.*;
import org.apache.spark.sql.DataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.apache.spark.sql.functions.lit;

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
        DataFrame data = CsvUtil.getDataFrameFromKaggleCsv(filePath, sqlContext, 2).select("ID", "features", "label");
        double f = 0;
        if (conf != null) {
            f = conf.getFractionTest();
        }
        if (f>0) {
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
        DataFrame data = CsvUtil.getDataFrameFromKaggleCsv(filePath, sqlContext, 1).select("ID", "features").withColumn("label", lit(0.0));
        return super.setTest(data);
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
        System.out.println("\nprint data test");
        dataSet.getTest().printSchema();
        predictions = ((PipelineModel) model).transform(dataSet.getTest());
        return super.test();
    }

    @Override
    public Map<String, Object> getResults() {
        predictions.select("ID", "predictedLabel").show();
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("indexedLabel")
                .setPredictionCol("prediction")
                .setMetricName("precision");
        double accuracy = evaluator.evaluate(predictions);
        System.out.println("Accuracy = " + (accuracy * 100));
        Map<String, Object> responses = new HashMap<>();
        responses.put("accuracy", accuracy);
        return responses;
    }

    @Override
    public void produce() {
        //predictions.select("ID", "predictedLabel").show();
    }
}