package com.minhdd.app.ml.controller;


import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.example.*;
import com.minhdd.app.ml.domain.MLConfiguration;
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
    LogisticRegressionService logisticRegressionService;

    @Inject
    DecisionTreeClassifierService decisionTreeClassifierService;

    @Inject
    RandomForestClassifierService randomForestClassifierService;

    @Inject
    GradientBoostedTreeClassifierService gradientBoostedTreeClassifierService;

    @Inject
    MultilayerPerceptronClassifierService multilayerPerceptronClassifierService;

    //Logistic regression
    @RequestMapping(value = "/lor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> lor() {
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setRegParam(0.3).setElasticNetParam(0.8);
        logisticRegressionService.loadFile("libsvm", "data/mllib/sample_libsvm_data.txt");
        return new ResponseEntity<>(logisticRegressionService.loadData().configure(conf).train().getResults(), HttpStatus.OK);
    }

    //Decision tree classifier
    @RequestMapping(value = "/dtc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> dtc() {
        decisionTreeClassifierService.loadFile("libsvm", "data/mllib/sample_libsvm_data.txt");
        return new ResponseEntity<>(decisionTreeClassifierService.loadData().train().test().getResults(), HttpStatus.OK);
    }

    //Random forest classifier
    @RequestMapping(value = "/rfc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> rfc() {
        randomForestClassifierService.loadFile("libsvm", "data/mllib/sample_libsvm_data.txt");
        return new ResponseEntity<>(randomForestClassifierService.loadData().train().test().getResults(), HttpStatus.OK);
    }

    //Gradient-boosted tree classifier
    @RequestMapping(value = "/gtc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> gtc() {
        gradientBoostedTreeClassifierService.loadFile("libsvm", "data/mllib/sample_libsvm_data.txt");
        return new ResponseEntity<>(gradientBoostedTreeClassifierService.loadData().train().test().getResults(), HttpStatus.OK);
    }

    //Multilayer perceptron classifier
    @RequestMapping(value = "/mpc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> mpc() {
        multilayerPerceptronClassifierService.loadFile("libsvm", "data/mllib/sample_multiclass_classification_data.txt");
        return new ResponseEntity<>(multilayerPerceptronClassifierService.loadData().train().test().getResults(), HttpStatus.OK);
    }

}
