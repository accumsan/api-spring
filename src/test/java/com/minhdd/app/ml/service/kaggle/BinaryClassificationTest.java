package com.minhdd.app.ml.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.service.classifier.BinaryClassificationService;
import org.apache.spark.SparkContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * Created by mdao on 07/03/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
public class BinaryClassificationTest {
    private final String TEST_FILE = "data/mllib/sample_binary_classification_data.txt";
    @Inject SparkContext sparkContext;
    MLService binaryClassificationService;

    @Before
    public void init() {
        binaryClassificationService = new BinaryClassificationService().context(null, sparkContext);
    }

    @Test
    public void trainAndTest() {
        binaryClassificationService.setFile(null, TEST_FILE, null, null);
        MLConfiguration conf = new MLConfiguration().setFractionTest(0.4);
        binaryClassificationService.configure(conf).loadData().train().test().getResults();
    }

}
