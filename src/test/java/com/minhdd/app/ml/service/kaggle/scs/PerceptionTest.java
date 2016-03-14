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
public class PerceptionTest {
    MLService perceptron;
    @Inject
    SQLContext sqlContext;
    @Inject
    SparkContext sparkContext;

    @Before
    public void init() {
        perceptron = new SCSPerceptronClassifier().context(sqlContext, sparkContext);
    }

    @Test
    public void trainAndTest() {
        perceptron.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        NeuralNetworkConfiguration nnConf = new NeuralNetworkConfiguration().setLayers(new int[]{4, 5, 4, 3}).setBlockSize(128).setSeed(1234L);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(100).setNeuralNetworkConfiguration(nnConf);
        perceptron.configure(conf).loadData().train().test().getResults();
    }

}
