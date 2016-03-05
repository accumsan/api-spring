package com.minhdd.app.ml.service;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
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
 * http://spark.apache.org/docs/latest/ml-classification-regression.html#multilayer-perceptron-classifier
 * Feedforward neural network
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class MultilayerPerceptronClassifierService extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(MultilayerPerceptronClassifierService.class);

    @Inject
    private SQLContext sqlContext;

    @Override
    public MLService loadData() {
        DataFrame data = loadFile(sqlContext);
        DataFrame[] splits = data.randomSplit(new double[]{0.6, 0.4}, 1234L);
        DataFrame trainingData = splits[0];
        DataFrame testData = splits[1];
        return super.loadData(data, trainingData, null, testData);
    }

    @Override
    protected MLAlgorithm<MultilayerPerceptronClassificationModel> algorithm() {
        int[] layers = new int[] {4, 5, 4, 3};
        MultilayerPerceptronClassifier trainer = new MultilayerPerceptronClassifier()
                .setLayers(layers)
                .setBlockSize(128)
                .setSeed(1234L)
                .setMaxIter(100);

        return (DataFrame training) -> trainer.fit(training);
    }

    @Override
    protected DataFrame transform(DataFrame test) {
        return ((MultilayerPerceptronClassificationModel) model).transform(test);
    }

    @Override
    public Map<String, Object> getResults() {
        DataFrame predictionAndLabels = predictions.select("prediction", "label");
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setMetricName("precision");
        logger.info("Precision = " + evaluator.evaluate(predictionAndLabels));
        Map<String, Object> responses = new HashMap<>();
        responses.put("precision", evaluator.evaluate(predictionAndLabels));
        return responses;
    }
}
