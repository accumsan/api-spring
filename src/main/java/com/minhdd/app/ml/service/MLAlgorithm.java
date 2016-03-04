package com.minhdd.app.ml.service;

/**
 * Created by mdao on 04/03/2016.
 */
interface MLAlgorithm<T, R> {
    R fit(T o);
}
