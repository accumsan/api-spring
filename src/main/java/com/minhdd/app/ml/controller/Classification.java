package com.minhdd.app.ml.controller;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.NeuralNetworkConfiguration;
import com.minhdd.app.ml.service.classifier.DecisionTreeClassifierService;
import com.minhdd.app.ml.service.classifier.GradientBoostedTreeClassifierService;
import com.minhdd.app.ml.service.classifier.LogisticRegressionService;
import com.minhdd.app.ml.service.classifier.MultilayerPerceptronClassifierService;
import com.minhdd.app.ml.service.classifier.OneVsRestClassifierService;
import com.minhdd.app.ml.service.classifier.RandomForestClassifierService;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by minhdao on 18/02/16.
 */
@RestController
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
@RequestMapping("/spark/class")
public class Classification {
    private final Logger logger = LoggerFactory.getLogger(Classification.class);
    @Inject
    SparkContext sparkContext;
    @Inject SQLContext sqlContext;

    //Logistic regression
    @RequestMapping(value = "/lor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> lor() {
        MLService logisticRegressionService = new LogisticRegressionService().context(sqlContext, sparkContext);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setRegParam(0.3).setElasticNetParam(0.8);
        logisticRegressionService.setFile(null, "libsvm", "data/mllib/sample_libsvm_data.txt", null, null);
        return new ResponseEntity<>(logisticRegressionService.loadData().configure(conf).train().getResults(), HttpStatus.OK);
    }

    //Decision tree classifier
    @RequestMapping(value = "/dtc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> dtc() {
        MLService decisionTreeClassifierService = new DecisionTreeClassifierService().context(sqlContext, sparkContext);
        decisionTreeClassifierService.setFile(null, "libsvm", "data/mllib/sample_libsvm_data.txt", null, null);
        return new ResponseEntity<>(decisionTreeClassifierService.loadData().train().test().getResults(), HttpStatus.OK);
    }

    //Random forest classifier
    @RequestMapping(value = "/rfc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> rfc() {
        MLService randomForestClassifierService = new RandomForestClassifierService().context(sqlContext, sparkContext);
        randomForestClassifierService.setFile(null, "libsvm", "data/mllib/sample_libsvm_data.txt", null, null);
        return new ResponseEntity<>(randomForestClassifierService.loadData().train().test().getResults(), HttpStatus.OK);
    }

    //Gradient-boosted tree classifier
    @RequestMapping(value = "/gtc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> gtc() {
        MLService gradientBoostedTreeClassifierService = new GradientBoostedTreeClassifierService().context(sqlContext, sparkContext);
        gradientBoostedTreeClassifierService.setFile(null, "libsvm", "data/mllib/sample_libsvm_data.txt", null, null);
        return new ResponseEntity<>(gradientBoostedTreeClassifierService.loadData().train().test().getResults(), HttpStatus.OK);
    }

    //Multilayer perceptron classifier
    @RequestMapping(value = "/mpc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> mpc() {
        MLService multilayerPerceptronClassifierService = new MultilayerPerceptronClassifierService().context(sqlContext, sparkContext);
        NeuralNetworkConfiguration nnConf = new NeuralNetworkConfiguration().setLayers(new int[]{4, 5, 4, 3}).setBlockSize(128).setSeed(1234L);
        MLConfiguration conf = new MLConfiguration().setMaxIteration(100).setNeuralNetworkConfiguration(nnConf);
        multilayerPerceptronClassifierService.setFile(null, "libsvm", "data/mllib/sample_multiclass_classification_data.txt", null, null);
        return new ResponseEntity<>(multilayerPerceptronClassifierService.loadData().configure(conf).train().test().getResults(), HttpStatus.OK);
    }

    //One-vs-Rest classifier (a.k.a. One-vs-All)
    @RequestMapping(value = "/ovr", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> ovr() {
        MLService oneVsRestClassifierService = new OneVsRestClassifierService().context(sqlContext, sparkContext);
        MLConfiguration conf = new MLConfiguration().setFractionTest(0.2).setTol(1E-6);
        oneVsRestClassifierService.setFile(null, "libsvm", "data/mllib/sample_multiclass_classification_data.txt", null, null);
        return new ResponseEntity<>(oneVsRestClassifierService.configure(conf).loadData().train().test().getResults(), HttpStatus.OK);
    }


}
