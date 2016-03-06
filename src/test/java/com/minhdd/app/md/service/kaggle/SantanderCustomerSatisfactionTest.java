package com.minhdd.app.md.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.service.kaggle.SantanderCustomerSatisfaction;
import org.apache.spark.sql.SQLContext;
import org.junit.Before;
import org.junit.BeforeClass;
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
        santanderCustomerSatisfaction.setFile(null, "data/kaggle/santander-customer-satisfaction/train.csv");
        MLConfiguration conf = new MLConfiguration().setFractionTest(0.5);
        santanderCustomerSatisfaction.configure(conf).loadData().train().test().getResults();
    }

    @Test
    public void trainAndProduce() {
        santanderCustomerSatisfaction.setFile(null, "data/kaggle/santander-customer-satisfaction/train.csv");
        santanderCustomerSatisfaction.configure(null).loadData().train();
        santanderCustomerSatisfaction.setFile(null, "data/kaggle/santander-customer-satisfaction/test.csv");
        santanderCustomerSatisfaction.loadTest().test().getResults();
    }

    //TODO
//    @Test
//    public void trainAndSave() {
//        santanderCustomerSatisfaction.setFile(null, "data/kaggle/santander-customer-satisfaction/train-1.csv");
//        santanderCustomerSatisfaction.configure(null).loadData().train().save();
//    }
//
//    @Test
//    public void getSaveAndTest() {
//        santanderCustomerSatisfaction.restore();
//        santanderCustomerSatisfaction.setFile(null, "data/kaggle/santander-customer-satisfaction/test-1.csv");
//        santanderCustomerSatisfaction.loadTest().test().produce();
//    }


}

