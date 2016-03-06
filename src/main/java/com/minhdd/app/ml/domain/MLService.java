package com.minhdd.app.ml.domain;

import org.apache.spark.sql.SQLContext;

import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 */

public interface MLService {
    MLService sqlContext(SQLContext sqlContext);
    MLService setFile(String fileType, String filePath);
    MLService loadData();
    MLService loadTest();
    MLService configure(MLConfiguration configuration);
    MLService train();
    MLService test();
    Map<String, Object> getResults();
    void save();
    void restore();
    void produce();

}
