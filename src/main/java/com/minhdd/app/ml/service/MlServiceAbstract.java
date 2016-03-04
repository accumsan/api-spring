package com.minhdd.app.ml.service;

/**
 * Created by mdao on 04/03/2016.
 */
public abstract class MlServiceAbstract implements MLService {
    private String filePath;
    private String fileType;
    private MLConfiguration configuration;
    protected Object model;

    protected String getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public MLConfiguration getConfiguration() {
        return configuration;
    }

    protected abstract Object loadDataSet();
    protected abstract MLAlgorithm algorithm();

    @Override
    public MLService loadFile(String fileType, String filePath) {
        this.filePath = filePath;
        this.fileType = fileType;
        return this;
    }

    @Override
    public MLService configure(MLConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public MLService train() {
        model = algorithm().fit(loadDataSet());
        return this;
    }
}
