package com.minhdd.app.ml.service;

import org.apache.spark.sql.DataFrame;

/**
 * Created by mdao on 04/03/2016.
 */
public interface MLAlgorithm<R> {
    R fit(DataFrame o);
}
