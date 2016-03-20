package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.domain.MLService;
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
 * Created by minhdao on 06/03/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
public class LogisticRegressionTest {
    MLService scfRegression;
    @Inject SQLContext sqlContext;
    @Inject SparkContext sparkContext;

    @Before
    public void init() {
        scfRegression = new SantanderCustomerSatisfactionRegression().context(sqlContext, sparkContext);
    }

    /****
     * * Test using Logistic Regression : Modify training file input and params
     ****/

    @Test
    public void trainAndTest() {
        scfRegression.setFile(null, null, FilesConstants.TRAIN_60, FilesConstants.VALIDATION_20, FilesConstants.TEST_20);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(100);
        scfRegression.configure(conf).loadData().train().getResults();
    }

}

