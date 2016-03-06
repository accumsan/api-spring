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

    private final String TRAIN_SAMPLE = "data/kaggle/santander-customer-satisfaction/train-1.csv";
    private final String TRAIN_KAGGLE = "/Users/minhdao/Workspace/ml/kaggle/santander-customer-satisfaction/data/train.csv";
    private final String TEST_SAMPLE = "data/kaggle/santander-customer-satisfaction/test-1.csv";
    private final String TEST_KAGGLE = "/Users/minhdao/Workspace/ml/kaggle/santander-customer-satisfaction/data/test.csv";
    private final String OUTPUT_DIR = "data/kaggle/santander-customer-satisfaction/save/";
    private final String RFP_MODEL = OUTPUT_DIR + "random-forest-pipeline.model";
    private final String TEST_OUTPUT = OUTPUT_DIR + "predictions.csv";


    @Inject
    SantanderCustomerSatisfaction santanderCustomerSatisfaction;

    @Inject
    SQLContext sqlContext;

    @Before
    public void init() {
        santanderCustomerSatisfaction.sqlContext(sqlContext);
    }

    @Test
    public void trainAndTest() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration().setFractionTest(0.5);
        santanderCustomerSatisfaction.configure(conf).loadData().train().test().getResults();
    }

    @Test
    public void trainAndProduce() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        santanderCustomerSatisfaction.configure(null).loadData().train();
        santanderCustomerSatisfaction.setFile(null, TEST_SAMPLE);
        santanderCustomerSatisfaction.loadTest().test().produce(TEST_OUTPUT);
    }


    @Test
    public void trainAndSave() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        santanderCustomerSatisfaction.configure(null).loadData().train().save(RFP_MODEL);
    }

    @Test
    public void getSaveAndProduce() {
        santanderCustomerSatisfaction.restore(RFP_MODEL);
        santanderCustomerSatisfaction.setFile(null, TEST_SAMPLE);
        santanderCustomerSatisfaction.loadTest().test().produce(TEST_OUTPUT);
    }


}

