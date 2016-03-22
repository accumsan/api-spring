package com.minhdd.app.ml.domain;

import org.apache.spark.SparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;

/**
 * Created by mdao on 04/03/2016.
 */
public abstract class MlServiceAbstract implements MLService {
    protected SQLContext sqlContext;
    protected SparkContext sparkContext;
    protected String fileType;
    protected StructType schema;
    protected String trainPath;
    protected String validationPath;
    protected String testPath;
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

    @Override public MLService schema(StructType schema) {
        this.schema = schema;
        return this;
    }

    @Override public MLService setFile(StructType schema, String fileType, String trainPath, String validationPath, String testPath) {
        this.schema = schema;
        this.fileType = fileType;
        this.trainPath = trainPath;
        this.validationPath = validationPath;
        this.testPath = testPath;
        return this;
    }

    /** load data **/

    protected MLService loadFile(double fraction, long l) {
        DataFrame data = sqlContext.read().format(fileType).load(trainPath);
        double f = fraction;
        if ((conf != null) && (conf.getFractionTest() > 0)) {
            f = conf.getFractionTest();
        }
        if (f > 0) {
            DataFrame[] splits = data.randomSplit(new double[]{1-f, f}, l);
            DataFrame trainingData = splits[0];
            DataFrame testData = splits[1];
            return loadData(data, trainingData, testData, testData);
        } else {
            return loadData(data);
        }

    }

    @Override public MLService loadInput(String inputPath) {
        DataFrame data = sqlContext.read().format(fileType).load(inputPath);
        return setInput(data);
    }

    protected MLService setInput(Object data) {
        if (dataSet == null) {
            dataSet = new DataSet(data, null, null, null);
        }
        dataSet.setInput(data);
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
        MLAlgorithm algorithm = algorithm();
        if (dataSet != null) {
            model = algorithm.fit(dataSet.getTraining());
        }
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
