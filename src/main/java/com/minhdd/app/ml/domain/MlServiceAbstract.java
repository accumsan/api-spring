package com.minhdd.app.ml.domain;

import org.apache.spark.SparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

/**
 * Created by mdao on 04/03/2016.
 */
public abstract class MlServiceAbstract implements MLService {
    protected SQLContext sqlContext;
    protected SparkContext sparkContext;
    protected String fileType;
    protected String filePath;
    protected MLConfiguration conf;
    protected DataSet dataSet;
    protected Object model;
    protected Object predictions;

    @Override public MLService configure(MLConfiguration configuration) {
        this.conf = configuration;
        return this;
    }

    @Override public MLService context(SQLContext sqlContext, SparkContext sc) {
        this.sqlContext = sqlContext;
        this.sparkContext = sc;
        return this;
    }

    @Override public MLService setFile(String fileType, String filePath) {
        this.filePath = filePath;
        this.fileType = fileType;
        return this;
    }

    /** load data **/

    protected DataFrame loadFile() {
        return sqlContext.read().format(fileType).load(filePath);
    }

    @Override public MLService loadTest() {
        DataFrame data = sqlContext.read().format(fileType).load(filePath);
        return setTest(data);
    }

    protected MLService setTest(Object data) {
        if (dataSet == null) {
            dataSet = new DataSet(data, null, null, data);
        } else {
            dataSet.setTest(data);
        }
        return this;
    }

    protected MLService loadData(Object data, Object training, Object cross, Object test) {
        dataSet = new DataSet(data, training, cross, test);
        return this;
    }

    protected MLService loadData(Object data) {
        dataSet = new DataSet(data, data, null, null);
        return this;
    }

    /** train and test **/
    @Override public MLService train() {
        model = algorithm().fit(dataSet.getTraining());
        return this;
    }

    protected abstract MLAlgorithm algorithm();
    protected DataFrame transform() {
        return null;
    }
    @Override public MLService test() {
        return this;
    }
    @Override public void save(String filePath) {}
    @Override public void restore(String filePath) {}
    @Override public void produce(String output) {}



}
