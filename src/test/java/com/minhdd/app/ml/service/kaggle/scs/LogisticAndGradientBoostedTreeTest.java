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
 * Created by minhdao on 21/03/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
public class LogisticAndGradientBoostedTreeTest {
    MLService scf;
    @Inject
    SQLContext sqlContext;
    @Inject
    SparkContext sparkContext;

    @Before
    public void init() {
        scf = new LogisticAndGradientBoostedTree().context(sqlContext, sparkContext);
    }

    @Test
    public void trainAndTest() {
        scf.setFile(null, null, FilesConstants.TRAIN_50, FilesConstants.VALIDATION_40, FilesConstants.TEST_10);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(50);
        scf.configure(conf).train().getResults();
    }
}
