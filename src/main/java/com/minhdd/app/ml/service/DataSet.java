package com.minhdd.app.ml.service;

import org.apache.spark.sql.DataFrame;

/**
 * Created by minhdao on 05/03/16.
 */
public class DataSet {
    public DataFrame getTraining() {
        return training;
    }

    public void setTraining(DataFrame training) {
        this.training = training;
    }

    public DataFrame getCrossValidation() {
        return crossValidation;
    }

    public void setCrossValidation(DataFrame crossValidation) {
        this.crossValidation = crossValidation;
    }

    public DataFrame getTest() {
        return test;
    }

    public void setTest(DataFrame test) {
        this.test = test;
    }

    public DataSet(DataFrame training, DataFrame crossValidation, DataFrame test) {
        this.training = training;
        this.crossValidation = crossValidation;
        this.test = test;
    }

    private DataFrame training;
    private DataFrame crossValidation;
    private DataFrame test;
}
