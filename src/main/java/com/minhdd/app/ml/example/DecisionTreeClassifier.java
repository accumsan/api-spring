package com.minhdd.app.ml.example;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.service.MLAlgorithm;
import com.minhdd.app.ml.service.MLService;
import com.minhdd.app.ml.service.MlServiceAbstract;
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
public class DecisionTreeClassifier extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(DecisionTreeClassifier.class);

    @Inject
    private SQLContext sqlContext;

    @Override
    public MLService loadData() {
        DataFrame data = loadFile(sqlContext);
        DataFrame[] splits = data.randomSplit(new double[]{0.7, 0.3});
        DataFrame trainingData = splits[0];
        DataFrame testData = splits[1];
        return super.loadData(trainingData, null, testData);
    }

    @Override
    protected MLAlgorithm algorithm() {
        return null;
    }

    @Override
    public Map<String, Object> getResults() {

        Map<String, Object> responses = new HashMap<>();

        return responses;
    }
}
