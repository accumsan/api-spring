package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
        return null;
    }

    @Override
    protected MLAlgorithm algorithm() {
        JavaRDD<LabeledPoint> train = ScsUtil.getLabeledPointJavaRDDFromCsv(trainPath, sqlContext, "TARGET", false);
        JavaRDD<LabeledPoint> validation = ScsUtil.getLabeledPointJavaRDDFromCsv(validationPath, sqlContext, "TARGET", false);
        JavaRDD<LabeledPoint> test = ScsUtil.getLabeledPointJavaRDDFromCsv(testPath, sqlContext, "TARGET", false);
        return null;
    }

    @Override
    public Map<String, Object> getResults() {
        return null;
    }
}
