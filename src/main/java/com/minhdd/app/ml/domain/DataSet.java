package com.minhdd.app.ml.domain;

/**
 * Created by minhdao on 05/03/16.
 */
public class DataSet<R> {
    public R getData() {
        return data;
    }

    public R getTraining() {
        return training;
    }

    public R getCrossValidation() {
        return crossValidation;
    }

    public R getTest() {
        return test;
    }

    public void setTest(R test) {
        this.test = test;
    }

    public DataSet(R data, R training, R cross, R test) {
        this.data = data;
        this.training = training;
        this.crossValidation = cross;
        this.test = test;
    }

    private R data;
    private R training;
    private R crossValidation;
    private R test;
}
