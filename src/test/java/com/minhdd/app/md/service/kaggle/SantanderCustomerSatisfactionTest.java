package com.minhdd.app.md.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.domain.MLConstants;
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
    private final String LOCAL_DIR_QS = "/Users/mdao/ws/minh/ml/kaggle/santander-customer-satisfaction/";
    private final String LOCAL_DIR_MAC86 = "/Users/minhdao/Workspace/ml/kaggle/santander-customer-satisfaction/data/";

    private final String TRAIN_SAMPLE = "data/kaggle/santander-customer-satisfaction/train-1.csv";
    private final String TEST_SAMPLE = "data/kaggle/santander-customer-satisfaction/test-1.csv";
    private final String TRAIN_KAGGLE = LOCAL_DIR_MAC86 + "train.csv";
    private final String TEST_KAGGLE = LOCAL_DIR_MAC86 + "test.csv";
    private final String OUTPUT_DIR = "data/kaggle/santander-customer-satisfaction/save/";
    private final String RFP_MODEL = OUTPUT_DIR + "random-forest-pipeline.model";
    private final String GBT_MODEL = OUTPUT_DIR + "gradient-boosted-pipeline.model";
    private final String LR_MODEL = OUTPUT_DIR + "logistic-regression.model";
    private final String BinaryClassification_MODEL = OUTPUT_DIR + "binary-classification.model";
    private final String TEST_OUTPUT = OUTPUT_DIR + "predictions.csv";


    @Inject
    SantanderCustomerSatisfaction santanderCustomerSatisfaction;

    @Inject
    SantanderCustomerSatisfactionRegression scfRegression;

    @Inject
    SantanderCustomerSatisfactionBinaryClassification scfBinaryClassification;

    @Inject
    SQLContext sqlContext;

    @Inject
    SparkContext sparkContext;

    @Before
    public void init() {
        santanderCustomerSatisfaction.context(sqlContext, sparkContext);
        scfRegression.context(sqlContext, sparkContext);
        scfBinaryClassification.context(sqlContext, sparkContext);
    }


    /****
     * * Production : Modify model and test file input
     ****/

    @Test
    public void getSavedAndProduce() {
        santanderCustomerSatisfaction.restore(RFP_MODEL);
        santanderCustomerSatisfaction.setFile(null, TEST_SAMPLE);
        santanderCustomerSatisfaction.loadTest().test().produce(TEST_OUTPUT);
    }

    /****
     * * Test using Binary Classification : Modify training file input
     ****/

    @Test
    public void trainWithBinaryClassificationAndTest() {
        scfBinaryClassification.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration()
                .setFractionTest(0.4).setAlgorithm(MLConstants.BinaryClassification);
        scfBinaryClassification.configure(conf).loadData().train().test().getResults();
    }

    @Test
    public void trainWithBinaryClassificationAndProduce() {
        scfBinaryClassification.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration().setFractionTest(0.2).setAlgorithm(MLConstants.BinaryClassification);
        scfBinaryClassification.configure(conf).loadData().train().save(BinaryClassification_MODEL);
        scfBinaryClassification.setFile(null, TEST_KAGGLE);
        scfBinaryClassification.loadTest().test().produce(TEST_OUTPUT);
    }

    @Test
    public void getSavedBinaryClassificationAndProduce() {
        scfBinaryClassification.restore(BinaryClassification_MODEL);
        scfBinaryClassification.setFile(null, TEST_KAGGLE);
        scfBinaryClassification.loadTest().test().produce(TEST_OUTPUT);
    }

    /****
     * * Test using Logistic Regression : Modify training file input and params
     ****/

    @Test
    public void trainWithLogisticRegressionAndTest() {
        scfRegression.setFile(null, TRAIN_SAMPLE);
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
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration().setFractionTest(0.3).setAlgorithm(MLConstants.RandomForest);
        santanderCustomerSatisfaction.configure(conf).loadData().train().test().getResults();
    }

    @Test
    public void trainAndProduce() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration().setAlgorithm(MLConstants.RandomForest);
        santanderCustomerSatisfaction.configure(conf).loadData().train();
        santanderCustomerSatisfaction.setFile(null, TEST_SAMPLE);
        santanderCustomerSatisfaction.loadTest().test().produce(TEST_OUTPUT);
    }

    //Modify training file input, max iteration
    @Test
    public void trainAndSave() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration().setAlgorithm(MLConstants.RandomForest);
        santanderCustomerSatisfaction.configure(conf).loadData().train().save(RFP_MODEL);
    }

    /****
     * * Test using GradientBoostedTree : Modify training file input
     ****/

    @Test
    public void trainWithGradientBoostedAndTest() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration().setFractionTest(0.3).setMaxIteration(10).setAlgorithm(MLConstants.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train().test().getResults();
    }

    //Modify training file input, max iteration, test file input
    @Test
    public void trainWithGradientBoostedAndProduce() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setAlgorithm(MLConstants.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train();
        santanderCustomerSatisfaction.setFile(null, TEST_SAMPLE);
        santanderCustomerSatisfaction.loadTest().test().produce(TEST_OUTPUT);
    }

    //Modify training file input, max iteration
    @Test
    public void trainWithGradientBoostedAndSave() {
        santanderCustomerSatisfaction.setFile(null, TRAIN_KAGGLE);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setAlgorithm(MLConstants.GradientBoostedTree);
        santanderCustomerSatisfaction.configure(conf).loadData().train().save(GBT_MODEL);
    }

}

