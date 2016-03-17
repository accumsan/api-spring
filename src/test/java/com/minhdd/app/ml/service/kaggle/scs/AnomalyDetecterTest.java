package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.NeuralNetworkConfiguration;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * Created by minhdao on 14/03/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
public class AnomalyDetecterTest {
    MLService anomalyDetector;
    @Inject
    SQLContext sqlContext;
    @Inject
    SparkContext sparkContext;

    @Before
    public void init() {
        anomalyDetector = new SCSAnomalyDetector().context(sqlContext, sparkContext);
    }

    @Test
    public void trainAndTest() {
        anomalyDetector.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        anomalyDetector.configure(null).loadData().train().getResults();
    }

}
