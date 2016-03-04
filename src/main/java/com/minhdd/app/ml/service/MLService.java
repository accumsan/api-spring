package com.minhdd.app.ml.service;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 */

public interface MLService {
    void loadDataSet(String path);
    void configure();
    void train();
    Map<String, Object> getResults();
}
