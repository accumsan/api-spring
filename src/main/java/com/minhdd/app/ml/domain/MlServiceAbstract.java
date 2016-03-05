package com.minhdd.app.ml.domain;

import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

/**
 * Created by mdao on 04/03/2016.
 */
public abstract class MlServiceAbstract implements MLService {
    private String filePath;
    private String fileType;
    private MLConfiguration configuration;
    protected DataSet dataSet;
    protected Object model;
    protected DataFrame predictions;

    protected String getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public MLConfiguration conf() {
        return configuration;
    }

    @Override
    public MLService loadFile(String fileType, String filePath) {
        this.filePath = filePath;
        this.fileType = fileType;
        return this;
    }

    protected DataFrame loadFile(SQLContext sqlContext) {
        return sqlContext.read().format(getFileType()).load(getFilePath());
    }

    protected MLService loadData(DataFrame data, DataFrame training, DataFrame cross, DataFrame test) {
        this.dataSet = new DataSet(data, training, cross, test);
        return this;
    }

    protected MLService loadData(DataFrame data) {
        this.dataSet = new DataSet(data, data, null, null);
        return this;
    }

    @Override
    public MLService configure(MLConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public MLService train() {
        model = algorithm().fit(dataSet.getTraining());
        return this;
    }

    protected abstract MLAlgorithm algorithm();

    protected DataFrame transform(DataFrame test) {
        return null;
    }

    @Override
    public MLService test() {
        predictions = transform(dataSet.getTest());
        return this;
    }


}
