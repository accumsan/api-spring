package com.minhdd.app.ml;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.NeuralNetworkConfiguration;
import com.minhdd.app.ml.service.classifier.LogisticRegressionService;
import com.minhdd.app.ml.service.classifier.MultilayerPerceptronClassifierService;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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
        logisticRegressionService.setFile(null, "libsvm", "data/mllib/sample_libsvm_data.txt", null, null);
        logisticRegressionService.loadData().configure(conf).train().getResults();
    }

    @Test
    public void perceptron() {
        MLService multilayerPerceptronClassifierService = new MultilayerPerceptronClassifierService().context(sqlContext, sparkContext);
        NeuralNetworkConfiguration nnConf = new NeuralNetworkConfiguration().setLayers(new int[]{4, 5, 4, 3}).setBlockSize(128).setSeed(1234L);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(100).setNeuralNetworkConfiguration(nnConf);
        multilayerPerceptronClassifierService.setFile(null, "libsvm", "data/mllib/sample_multiclass_classification_data.txt", null, null);
        Map<String, Object> responses = multilayerPerceptronClassifierService.loadData().configure(conf).train().test().getResults();
        assertEquals(0.9215686274509803, responses.get("precision"));
    }
}
