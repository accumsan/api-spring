package com.minhdd.app.md.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.service.kaggle.SantanderCustomerSatisfaction;
import org.apache.spark.sql.SQLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * Created by minhdao on 06/03/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
public class SantanderCustomerSatisfactionTest {
    private final String LOCAL_DIR_QS = "/Users/mdao/ws/minh/ml/kaggle/santander-customer-satisfaction/";
    private final String LOCAL_DIR_MAC86 = "/Users/minhdao/Workspace/ml/kaggle/santander-customer-satisfaction/data/";

    private final String TRAIN_SAMPLE = "data/kaggle/santander-customer-satisfaction/train-1.csv";
    private final String TEST_SAMPLE = "data/kaggle/santander-customer-satisfaction/test-1.csv";
    private final String TRAIN_KAGGLE = LOCAL_DIR_QS + "train.csv";
    private final String TEST_KAGGLE = LOCAL_DIR_QS +"test.csv";
    private final String OUTPUT_DIR = "data/kaggle/santander-customer-satisfaction/save/";
    private final String RFP_MODEL = OUTPUT_DIR + "random-forest-pipeline.model";
    private final String GBT_MODEL = OUTPUT_DIR + "gradient-boosted-pipeline.model";
    private final String TEST_OUTPUT = OUTPUT_DIR + "predictions.csv";


    @Inject
    SantanderCustomerSatisfaction santanderCustomerSatisfaction;

    @Inject
    SQLContext sqlContext;

    @Before
    public void init() {
        santanderCustomerSatisfaction.sqlContext(sqlContext);
    }


    /****
     ** Production : Modify model and test file input
     * ****/

    @Test
    public void getSavedAndProduce() {
        santanderCustomerSatisfaction.restore(GBT_MODEL);
        santanderCustomerSatisfaction.setFile(null, TEST_SAMPLE);
        santanderCustomerSatisfaction.loadTest().test().produce(TEST_OUTPUT);
    }

    /****
     ** Test using Random Forest : Modify training file input
     * ****/

    @Test
    public void trainAndTest() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_SAMPLE);
        MLConfiguration conf = new MLConfiguration().setFractionTest(0.5);
        santanderCustomerSatisfaction.configure(conf).loadData().train().test().getResults();
    }

    @Test
    public void trainAndProduce() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        santanderCustomerSatisfaction.loadData().train();
        santanderCustomerSatisfaction.setFile(null, TEST_SAMPLE);
        santanderCustomerSatisfaction.loadTest().test().produce(TEST_OUTPUT);
    }

    //Modify training file input, max iteration
    @Test
    public void trainAndSave() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        santanderCustomerSatisfaction.loadData().train().save(RFP_MODEL);
    }

    /****
     ** Test using GradientBoostedTree : Modify training file input
     * ****/

    @Test
    public void trainWithGradientBoostedAndTest() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration().setFractionTest(0.5).setMaxIteration(10).setAlgorithm(MLConfiguration.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train().test().getResults();
    }

    //Modify training file input, max iteration, test file input
    @Test
    public void trainWithGradientBoostedAndProduce() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setAlgorithm(MLConfiguration.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train();
        santanderCustomerSatisfaction.setFile(null, TEST_SAMPLE);
        santanderCustomerSatisfaction.loadTest().test().produce(TEST_OUTPUT);
    }

    //Modify training file input, max iteration
    @Test
    public void trainWithGradientBoostedAndSave() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setAlgorithm(MLConfiguration.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train().save(GBT_MODEL);
    }

}

