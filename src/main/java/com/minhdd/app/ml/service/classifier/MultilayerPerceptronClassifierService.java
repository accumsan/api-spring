package com.minhdd.app.ml.service.classifier;

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
public class MultilayerPerceptronClassifierService extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(MultilayerPerceptronClassifierService.class);

    @Override
    public MLService loadData() {
        return loadFile(0.4, 1234L);
    }

    @Override
    protected MLAlgorithm<MultilayerPerceptronClassificationModel, DataFrame> algorithm() {
        int[] layers = new int[] {4, 5, 4, 3};
        MultilayerPerceptronClassifier trainer = new MultilayerPerceptronClassifier()
                .setLayers(conf.getNn().getLayers())
                .setBlockSize(conf.getNn().getBlockSize())
                .setSeed(conf.getNn().getSeed())
                .setMaxIter(conf.getMaxIteration());

        return (DataFrame training) -> trainer.fit(training);
    }

    @Override
    public MLService test() {
        predictions = ((MultilayerPerceptronClassificationModel) model).transform((DataFrame)dataSet.getTest());
        return super.test();
    }

    @Override
    public Map<String, Object> getResults() {
        DataFrame predictions = (DataFrame) this.predictions;
        DataFrame predictionAndLabels = predictions.select("prediction", "label");
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setMetricName("precision");
        logger.info("Precision = " + evaluator.evaluate(predictionAndLabels));
        Map<String, Object> responses = new HashMap<>();
        responses.put("precision", evaluator.evaluate(predictionAndLabels));
        return responses;
    }
}
