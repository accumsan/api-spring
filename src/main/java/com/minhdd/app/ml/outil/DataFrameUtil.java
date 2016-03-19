package com.minhdd.app.ml.outil;

import com.google.common.primitives.Doubles;
import com.minhdd.app.ml.service.kaggle.scs.FilesConstants;
import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.mllib.linalg.DenseMatrix;
import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;

import java.util.ArrayList;
import java.util.List;

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
        String[] columns = new String[data.columns().length - offset - FilesConstants.EXCLUDED_COLUMNS.size()];
        int i = 0;
        for (String column : data.columns()) {
            if (!column.equals("TARGET") && !column.equals("ID") && !FilesConstants.EXCLUDED_COLUMNS.contains(column)) {
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

    public static DataFrame assembled(DataFrame df, int offset, String out) {
        String[] columns = DataFrameUtil.getFeatureColumns(offset, df);
        VectorAssembler assembler = new VectorAssembler().setInputCols(columns).setOutputCol(out);
        return assembler.transform(df);
    }

    public static Vector extractVector(DataFrame df, String[] columns, long row) {
        DataFrame meanAssembled = new VectorAssembler().setInputCols(columns).setOutputCol("features").transform(df);
        return meanAssembled.collectAsList().get((int) row).getAs("features");
    }

    //return mean vector for multivariate gaussian distribution
    public static Vector mean(DataFrame input) {
        DataFrame meanDf = input.cube().avg();
        return extractVector(meanDf, meanDf.columns(), 0);
    }

    public static DataFrame subtractedByVector(DataFrame input, Vector v) {
        int n = input.first().length();
        if (n == v.size()) {
            String[] columns = input.columns();
            DataFrame output = input;
            for (int i = 0; i < n; i++) {
                output = output.withColumn(columns[i], input.col(columns[i]).minus(v.apply(i)));
            }
            return output;
        } else {
            return null;
        }
    }

    public static RowMatrix convertToRowMatrix(DataFrame df) {
        DataFrame assembled = new VectorAssembler().setInputCols(df.columns()).setOutputCol("features").transform(df);
        return new RowMatrix(assembled.toJavaRDD().map(row -> (Vector) row.getAs("features")).rdd());
    }

    public static DenseMatrix convertToDenseMatrix(DataFrame df) {
        int n = df.first().length();
        double[] values = Doubles.toArray(df.toJavaRDD().flatMap(row -> {
            List<Double> d = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                d.add(row.getDouble(i));
            }
            return d;
        }).collect());
        return (DenseMatrix) Matrices.dense(n, (int) df.count(), values);
    }

    //return sigma matrix for multivariate gaussian distribution
    public static Matrix sigma(DataFrame input, Vector mean) {
        DenseMatrix elementary_sigma = convertToDenseMatrix(subtractedByVector(input, mean));
        Matrix sigma = elementary_sigma.multiply(elementary_sigma.transpose());
        MatrixUtil.divide(sigma, input.count());
        return sigma;
    }

    private static String columnName(String prefix, int i) {
        return prefix + "_" + i;
    }

    public static DataFrame splitVectorColumn(SQLContext sqlContext, DataFrame df, String column, int length, String idColumn) {
        JavaRDD<Row> rowRDD = df.toJavaRDD().map(row -> {
            Vector v = row.getAs(column);
            Double[] splits = ArrayUtils.toObject(v.toArray());
            int id = row.getAs(idColumn);
            Double[] array = new Double[length + 1];
            array[0] = Double.valueOf(Integer.valueOf(id).doubleValue());
            for (int i = 0; i < length; i++) {
                array[i + 1] = splits[i];
            }
            return RowFactory.create(array);
        });
        List<StructField> fields = new ArrayList<>();
        fields.add(DataTypes.createStructField(idColumn, DataTypes.DoubleType, true));
        for (int i = 0; i < length; i++) {
            fields.add(DataTypes.createStructField(columnName(column, i), DataTypes.DoubleType, true));
        }
        DataFrame output = sqlContext.createDataFrame(rowRDD, DataTypes.createStructType(fields));
        return output.join(df, idColumn);
    }
}
