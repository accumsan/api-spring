package com.minhdd.app.ml.domain;

import org.apache.spark.sql.DataFrame;

/**
 * Created by mdao on 04/03/2016.
 */
public interface MLAlgorithm<R, T> {
    R fit(T o);
}
