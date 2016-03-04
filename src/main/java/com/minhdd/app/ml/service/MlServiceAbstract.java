package com.minhdd.app.ml.service;

/**
 * Created by mdao on 04/03/2016.
 */
public abstract class MlServiceAbstract implements MLService {
    protected String path;
    protected Object model;

    protected abstract Object loadDataSet();
    protected abstract MLAlgorithm algorithm();

    @Override
    public void loadFile(String path) {
        this.path = path;
    }

    @Override
    public void train() {
        model = algorithm().fit(loadDataSet());
    }
}
