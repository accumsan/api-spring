package com.minhdd.app.ml.service;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.ml.regression.LinearRegressionTrainingSummary;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 * http://spark.apache.org/docs/latest/ml-classification-regression.html#linear-regression
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class LinearRegressionService extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(LinearRegressionService.class);

    @Inject
    private SQLContext sqlContext;

    @Override
    public MLService loadData() {
        DataFrame data = loadFile(sqlContext);
        return super.loadData(data);
    }

    @Override
    protected MLAlgorithm<LinearRegressionModel> algorithm() {
        LinearRegression lr = new LinearRegression()
                .setMaxIter(conf().getMaxIteration())
                .setRegParam(conf().getRegParam())
                .setElasticNetParam(conf().getElasticNetParam());
        return (DataFrame training) -> lr.fit(training);
    }

    @Override
    public Map<String, Object> getResults() {
        LinearRegressionModel lrModel = (LinearRegressionModel) model;
        logger.info("Coefficients: " + lrModel.coefficients() + " Intercept: " + lrModel.intercept());
        // Summarize the model over the training set and print out some metrics
        LinearRegressionTrainingSummary trainingSummary = lrModel.summary();
        logger.info("numIterations: " + trainingSummary.totalIterations());
        logger.info("objectiveHistory: " + Vectors.dense(trainingSummary.objectiveHistory()));
        trainingSummary.residuals().show();
        logger.info("RMSE: " + trainingSummary.rootMeanSquaredError());
        logger.info("r2: " + trainingSummary.r2());

        Map<String, Object> responses = new HashMap<>();
        responses.put("coefficients", lrModel.coefficients().toString());
        responses.put("intercept", lrModel.intercept());
        responses.put("numIterations", trainingSummary.totalIterations());
        responses.put("RMSE", trainingSummary.rootMeanSquaredError());
        responses.put("r2", trainingSummary.r2());
        return responses;
    }
}
