package com.minhdd.app.ml.service;

import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 */

public interface MLService {
    MLService loadFile(String fileType, String filePath);
    MLService configure(MLConfiguration configuration);
    MLService train();
    Map<String, Object> getResults();
}
