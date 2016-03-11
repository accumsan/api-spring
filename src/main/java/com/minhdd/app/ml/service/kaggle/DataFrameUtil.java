package com.minhdd.app.ml.service.kaggle;

import org.apache.spark.sql.DataFrame;

/**
 * Created by minhdao on 10/03/16.
 */
public class DataFrameUtil {
    public static void splitToTrainAndCrossValidation(DataFrame df, double fraction, String trainFilePath, String crossFilePath) {
        DataFrame[] splits = df.randomSplit(new double[]{1 - fraction, fraction});
        DataFrame trainingData = splits[0];
        DataFrame crossValidationData = splits[1];
        CsvUtil.save(trainingData, trainFilePath, true);
        CsvUtil.save(crossValidationData, crossFilePath, true);
    }
}
