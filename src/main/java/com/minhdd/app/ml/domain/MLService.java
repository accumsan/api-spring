package com.minhdd.app.ml.domain;

import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;

import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 */

public interface MLService {
    MLService context(SQLContext sqlContext, SparkContext sc);
    MLService setFile(String fileType, String trainPath, String validationPath, String testPath);
    MLService loadData();
    MLService loadInput(String inputPath);
    MLService configure(MLConfiguration configuration);
    MLService train();
    MLService test();
    Map<String, Object> getResults();
    void save(String modelFilePath);
    void restore(String modelFilePath);
    void produce(String output);

}
