package com.minhdd.app.ml.controller;


import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.example.DecisionTreeClassifier;
import com.minhdd.app.ml.example.LogisticResgresion;
import com.minhdd.app.ml.service.MLConfiguration;
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
    LogisticResgresion logisticResgresion;

    @Inject
    DecisionTreeClassifier decisionTreeClassifier;

    @RequestMapping(value = "/lor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> lor() {
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setRegParam(0.3).setElasticNetParam(0.8);
        logisticResgresion.loadFile("libsvm", "data/mllib/sample_libsvm_data.txt");
        return new ResponseEntity<>(logisticResgresion.loadData().configure(conf).train().getResults(), HttpStatus.OK);
    }

    @RequestMapping(value = "/dtc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> dtc() {
        decisionTreeClassifier.loadFile("libsvm", "data/mllib/sample_libsvm_data.txt");
        return new ResponseEntity<>(decisionTreeClassifier.loadData().train().getResults(), HttpStatus.OK);
    }


}
