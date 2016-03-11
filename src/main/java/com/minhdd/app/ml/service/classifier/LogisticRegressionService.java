package com.minhdd.app.ml.service.classifier;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.SparkContext;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 * http://spark.apache.org/docs/latest/ml-classification-regression.html#logistic-regression
 */
public class LogisticRegressionService extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(LogisticRegressionService.class);

    @Override
    public MLService loadData() {
        DataFrame data = loadFile();
        return super.loadData(data);
    }

    @Override
    protected MLAlgorithm<LogisticRegressionModel, DataFrame> algorithm() {
        LogisticRegression lr = new LogisticRegression()
                .setMaxIter(conf.getMaxIteration())
                .setRegParam(conf.getRegParam())
                .setElasticNetParam(conf.getElasticNetParam());
        return (DataFrame training) -> lr.fit(training);
    }

    @Override
    public Map<String, Object> getResults() {
        LogisticRegressionModel lrModel = (LogisticRegressionModel) model;
        logger.info("Coefficients: " + lrModel.coefficients());
        logger.info("Intercept: " + lrModel.intercept());
        Map<String, Object> responses = new HashMap<>();
        responses.put("coefficients", lrModel.coefficients().toString());
        responses.put("intercept", lrModel.intercept());
        return responses;
    }
}
