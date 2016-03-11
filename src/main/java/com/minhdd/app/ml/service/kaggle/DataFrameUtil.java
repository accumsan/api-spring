package com.minhdd.app.ml.service.kaggle;

import org.apache.spark.sql.DataFrame;

/**
 * Created by minhdao on 10/03/16.
 */
public class DataFrameUtil {
    public static void splitToTwoDataSet(DataFrame df, double fraction, String firstFilePath, String secondFilePath) {
        DataFrame[] splits = df.randomSplit(new double[]{1 - fraction, fraction});
        DataFrame first = splits[0];
        DataFrame second = splits[1];
        CsvUtil.save(first, firstFilePath, true);
        CsvUtil.save(second, secondFilePath, true);
    }
    public static DataFrame randomFractioned(DataFrame df, double fraction) {
        return df.randomSplit(new double[]{fraction, 1 - fraction})[0];
    }
}
