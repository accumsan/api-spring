package com.minhdd.app.ml;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.service.classifier.LogisticRegressionService;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;
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
public class ClassificationTest {

    @Inject
    SparkContext sparkContext;
    @Inject
    SQLContext sqlContext;

    @Test
    public void logisticRegression() {
        MLService logisticRegressionService = new LogisticRegressionService().context(sqlContext, sparkContext);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setRegParam(0.3).setElasticNetParam(0.8);
        logisticRegressionService.setFile("libsvm", "data/mllib/sample_libsvm_data.txt", null, null);
        logisticRegressionService.loadData().configure(conf).train().getResults();
    }
}
