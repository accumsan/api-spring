package com.minhdd.app.ml.service.classifier;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import org.apache.spark.sql.DataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 * http://spark.apache.org/docs/latest/ml-classification-regression.html#logistic-regression
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class BinaryClassificationService extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(BinaryClassificationService.class);

    @Inject
    SparkContext sparkContext;

    @Override
    public MLService loadData() {
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sparkContext, filePath).toJavaRDD();
        double f = 0;
        if (conf != null) {
            f = conf.getFractionTest();
        }
        if (f>0) {
            JavaRDD<LabeledPoint>[] splits =
                    data.randomSplit(new double[]{1-f, f}, 11L);
            JavaRDD<LabeledPoint> training = splits[0].cache();
            JavaRDD<LabeledPoint> test = splits[1];
            return super.loadData(data, training, null, test);
        }
        return super.loadData(data);
    }

    @Override
    protected MLAlgorithm<LogisticRegressionModel, JavaRDD<LabeledPoint>> algorithm() {
        final LogisticRegressionWithLBFGS model = new LogisticRegressionWithLBFGS().setNumClasses(2);
        return (JavaRDD<LabeledPoint> training) -> model.run(training.rdd());
    }

    @Override
    public Map<String, Object> getResults() {
        LinearRegressionModel lrModel = (LinearRegressionModel) model;
        logger.info("Coefficients: " + lrModel.coefficients());
        logger.info("Intercept: " + lrModel.intercept());
        Map<String, Object> responses = new HashMap<>();
        responses.put("coefficients", lrModel.coefficients().toString());
        responses.put("intercept", lrModel.intercept());
        return responses;
    }
}
