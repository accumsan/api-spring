package com.minhdd.app.ml.service;

import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 */

public interface MLService {
    void loadFile(String path);
    void train();
    Map<String, Object> getResults();
}
