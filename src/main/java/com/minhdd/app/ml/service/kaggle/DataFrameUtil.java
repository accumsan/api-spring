package com.minhdd.app.ml.service.kaggle;

import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.types.DataTypes;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;

import static org.apache.spark.sql.functions.avg;

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

    public static String[] getFeatureColumns(int offset, DataFrame data) {
        String[] columns = new String[data.columns().length - offset];
        int i = 0;
        for (String column : data.columns()) {
            if (!column.equals("TARGET") && !column.equals("ID")) {
                columns[i++] = column;
            }
        }
        return columns;
    }

    public static List<String> getColumnsFromValue(DataFrame data, double value) {
        List<String> columns = new ArrayList();
        List<Integer> indexes = new ArrayList();
        for (int i = 1; i < data.columns().length; i++) {
            if (data.schema().apply(i).dataType().equals(DataTypes.DoubleType)) {
                indexes.add(i);
            }
        }
        data.collectAsList().forEach(row -> {
            for (int index : indexes) {
                if (row.getDouble(index) == value) {
                    System.out.println(index);
                    columns.add(data.columns()[index]);
                }
            }
        });
        return columns;
    }

    //return mean vector for multivariate gaussian distribution
    public static Vector mean(DataFrame input) {
        DataFrame meanDf = input.cube().avg().toDF();
        String[] columns = meanDf.columns();
        DataFrame meanAssembled = new VectorAssembler().setInputCols(columns).setOutputCol("features").transform(meanDf);
        Vector mean = meanAssembled.first().getAs("features");
        return mean;
    }

    public static void printArray(String[] strings) {
        for (String s : strings) {
            System.out.print(s);
        }
    }

    //return sigma matrix for multivariate gaussian distribution
    public static Matrix sigma(DataFrame train) {
        return null;
    }
}
