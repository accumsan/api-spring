package com.minhdd.app.ml;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.ml.regression.LinearRegressionTrainingSummary;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.execution.columnar.ObjectColumnStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by minhdao on 18/02/16.
 */
@RestController
@RequestMapping("/spark/ml/lr")
public class MlTestRessource {
    private final Logger logger = LoggerFactory.getLogger(MlTestRessource.class);

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> get(){
        JavaSparkContext sc = new JavaSparkContext("local", "basicmap", System.getenv("SPARK_HOME"), System.getenv("JARS"));
        SQLContext sqlContext = new SQLContext(sc);
        DataFrame training = sqlContext.read().format("libsvm").load("src/main/resources/data/mllib/sample_linear_regression_data.txt");

        LinearRegression lr = new LinearRegression()
                .setMaxIter(10)
                .setRegParam(0.3)
                .setElasticNetParam(0.8);

        // Fit the model
        LinearRegressionModel lrModel = lr.fit(training);

        // Print the coefficients and intercept for linear regression
        logger.info("Coefficients: " + lrModel.coefficients() + " Intercept: " + lrModel.intercept());

        // Summarize the model over the training set and print out some metrics
        LinearRegressionTrainingSummary trainingSummary = lrModel.summary();
        logger.info("numIterations: " + trainingSummary.totalIterations());
        logger.info("objectiveHistory: " + Vectors.dense(trainingSummary.objectiveHistory()));
        trainingSummary.residuals().show();
        logger.info("RMSE: " + trainingSummary.rootMeanSquaredError());
        logger.info("r2: " + trainingSummary.r2());

        //response
        Map<String, Object> response = new HashMap<>();
        response.put("intercept", lrModel.intercept());
        response.put("numIterations", trainingSummary.totalIterations());
        response.put("RMSE", trainingSummary.rootMeanSquaredError());
        response.put("r2", trainingSummary.r2());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
