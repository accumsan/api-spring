package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.domain.MLEnum;
import com.minhdd.app.ml.domain.MLService;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;
import org.junit.After;
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
    MLService santanderCustomerSatisfaction;
    @Inject
    SQLContext sqlContext;
    @Inject
    SparkContext sparkContext;

    @Before
    public void init() {
        santanderCustomerSatisfaction = new SantanderCustomerSatisfaction().context(sqlContext, sparkContext);
    }

    @After
    public void stop() {
        sparkContext.stop();
    }

    /****
     * * Production : Modify model and test file input
     ****/

    @Test
    public void getSavedAndProduce() {
        santanderCustomerSatisfaction.restore(FilesConstants.RFP_MODEL);
        santanderCustomerSatisfaction.loadInput(FilesConstants.TEST_KAGGLE).produce(FilesConstants.TEST_OUTPUT);
    }

    /****
     * * Test using Random Forest : Modify training file input
     ****/

    @Test
    public void trainWithRandomForestAndTest() {
        santanderCustomerSatisfaction.setFile(null, null, FilesConstants.TRAIN_KAGGLE, FilesConstants.VALIDATION_20, FilesConstants.TEST_20);
        MLConfiguration conf = new MLConfiguration().setAlgorithm(MLEnum.RandomForest);
        santanderCustomerSatisfaction.configure(conf).loadData().train().test().getResults();
    }

    @Test
    public void trainWithRandomForestAndProduce() {
        santanderCustomerSatisfaction.setFile(null, null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setAlgorithm(MLEnum.RandomForest);
        santanderCustomerSatisfaction.configure(conf).loadData().train();
        santanderCustomerSatisfaction.test().produce(FilesConstants.TEST_OUTPUT);
    }

    //Modify training file input, max iteration
    @Test
    public void trainWithRandomForestAndSave() {
        santanderCustomerSatisfaction.setFile(null, null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setAlgorithm(MLEnum.RandomForest);
        santanderCustomerSatisfaction.configure(conf).loadData().train().save(FilesConstants.RFP_MODEL);
    }

    /****
     * * Test using GradientBoostedTree : Modify training file input
     ****/

    @Test
    public void trainWithGradientBoostedAndTest() {
        santanderCustomerSatisfaction.setFile(null, null, FilesConstants.TRAIN_KAGGLE, FilesConstants.VALIDATION_20, FilesConstants.TEST_20);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(100).setAlgorithm(MLEnum.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train().test().getResults();
    }

    //Modify training file input, max iteration, test file input
    @Test
    public void trainWithGradientBoostedAndProduce() {
        santanderCustomerSatisfaction.setFile(null, null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setAlgorithm(MLEnum.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train();
        santanderCustomerSatisfaction.test().produce(FilesConstants.TEST_OUTPUT);
    }

    //Modify training file input, max iteration
    @Test
    public void trainWithGradientBoostedAndSave() {
        santanderCustomerSatisfaction.setFile(null, null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setAlgorithm(MLEnum.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train().save(FilesConstants.GBT_MODEL);
    }

}

