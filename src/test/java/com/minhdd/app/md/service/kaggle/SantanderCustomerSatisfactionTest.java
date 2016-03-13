package com.minhdd.app.md.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.domain.MLConstants;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.service.kaggle.SantanderCustomerSatisfaction;
import com.minhdd.app.ml.service.kaggle.SantanderCustomerSatisfactionBinaryClassification;
import com.minhdd.app.ml.service.kaggle.SantanderCustomerSatisfactionRegression;
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
public class SantanderCustomerSatisfactionTest {
    MLService santanderCustomerSatisfaction;
    MLService scfRegression;
    MLService scfBinaryClassification;
    @Inject SQLContext sqlContext;
    @Inject SparkContext sparkContext;

    @Before
    public void init() {
        santanderCustomerSatisfaction = new SantanderCustomerSatisfaction().context(sqlContext, sparkContext);
        scfRegression = new SantanderCustomerSatisfactionRegression().context(sqlContext, sparkContext);
        scfBinaryClassification = new SantanderCustomerSatisfactionBinaryClassification().context(sqlContext, sparkContext);
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
     * * Test using Binary Classification : Modify files input
     ****/

    @Test
    public void trainWithBinaryClassificationAndTest() {
        scfBinaryClassification.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        scfBinaryClassification.loadData().train().test().getResults();
    }

    @Test
    public void trainWithBinaryClassificationAndProduce() {
        scfBinaryClassification.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setAlgorithm(MLConstants.BinaryClassification);
        scfBinaryClassification.configure(conf).loadData().train().save(FilesConstants.BinaryClassification_MODEL);
        scfBinaryClassification.test().produce(FilesConstants.TEST_OUTPUT);
    }

    @Test
    public void getSavedBinaryClassificationAndProduce() {
        scfBinaryClassification.restore(FilesConstants.BinaryClassification_MODEL);
        scfBinaryClassification.loadInput(FilesConstants.TEST_KAGGLE).produce(FilesConstants.TEST_OUTPUT);
    }

    /****
     * * Test using Logistic Regression : Modify training file input and params
     ****/

    @Test
    public void trainWithLogisticRegressionAndTest() {
        scfRegression.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration()
                .setMaxIteration(10).setRegParam(0.5).setElasticNetParam(0.8)
                .setAlgorithm(MLConstants.LogisticRegression);
        scfRegression.configure(conf).loadData().train().getResults();
    }

    /****
     * * Test using Random Forest : Modify training file input
     ****/

    @Test
    public void trainAndTest() {
        santanderCustomerSatisfaction.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setAlgorithm(MLConstants.RandomForest);
        santanderCustomerSatisfaction.configure(conf).loadData().train().test().getResults();
    }

    @Test
    public void trainAndProduce() {
        santanderCustomerSatisfaction.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setAlgorithm(MLConstants.RandomForest);
        santanderCustomerSatisfaction.configure(conf).loadData().train();
        santanderCustomerSatisfaction.test().produce(FilesConstants.TEST_OUTPUT);
    }

    //Modify training file input, max iteration
    @Test
    public void trainAndSave() {
        santanderCustomerSatisfaction.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setAlgorithm(MLConstants.RandomForest);
        santanderCustomerSatisfaction.configure(conf).loadData().train().save(FilesConstants.RFP_MODEL);
    }

    /****
     * * Test using GradientBoostedTree : Modify training file input
     ****/

    @Test
    public void trainWithGradientBoostedAndTest() {
        santanderCustomerSatisfaction.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setAlgorithm(MLConstants.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train().test().getResults();
    }

    //Modify training file input, max iteration, test file input
    @Test
    public void trainWithGradientBoostedAndProduce() {
        santanderCustomerSatisfaction.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setAlgorithm(MLConstants.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train();
        santanderCustomerSatisfaction.test().produce(FilesConstants.TEST_OUTPUT);
    }

    //Modify training file input, max iteration
    @Test
    public void trainWithGradientBoostedAndSave() {
        santanderCustomerSatisfaction.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setAlgorithm(MLConstants.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train().save(FilesConstants.GBT_MODEL);
    }

}

