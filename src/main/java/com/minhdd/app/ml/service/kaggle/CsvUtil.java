package com.minhdd.app.ml.service.kaggle;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.feature.RFormula;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by minhdao on 06/03/16.
 */
public class CsvUtil {

    public static final String CSV_FORMAT = "com.databricks.spark.csv";

    public static DataFrame loadCsvFile(SQLContext sqlContext, String filePath, boolean header, boolean inferSchema) {
        return sqlContext.read().format(CSV_FORMAT)
                .option("inferSchema", inferSchema ? "true" : "false")
                .option("header", header ? "true" : "false")
                .load(filePath);
    }

    public static DataFrame getDataFrameFromKaggleCsv(String filePath, SQLContext sqlContext, int offset) {
        DataFrame data = loadCsvFile(sqlContext, filePath, true, true);
        String[] columns = getFeatureColumns(offset, data);
        //TODO normalisation
        //        Normalizer normalizer = new Normalizer()
//                .setInputCol("features")
//                .setOutputCol("normFeatures")
//                .setP(.0);
//        DataFrame df = normalizer.transform(dataFrame);
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(columns)
                .setOutputCol("features");
        return assembler.transform(data);
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

    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDD(DataFrame df) {
        return df.toJavaRDD().map(row -> new LabeledPoint(row.getInt(0), row.getAs(1)));
    }

    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDDFromKaggleCsv(String filePath, SQLContext sqlContext, int offset, String labelColName) {
        return getLabeledPointJavaRDD(getDataFrameFromKaggleCsv(filePath, sqlContext, offset).select(labelColName, "features"));
    }

    public static void save(DataFrame predictions, String output, boolean header) {
        try {
            FileDeleteStrategy.FORCE.delete(new File("temp"));
            FileDeleteStrategy.FORCE.delete(new File(output));
        } catch (IOException e) {
            System.out.println("File not deleted : temp");
        }
        predictions.repartition(1).write().format(CSV_FORMAT)
                .option("header", header ? "true" : "false")
                .option("delimiter", ",")
                .save("temp");
        File dir = new File("temp");
        (new File("temp/part-00000")).renameTo(new File(output));
        for (File file : dir.listFiles()) {
            try {
                FileDeleteStrategy.FORCE.delete(file);
            } catch (IOException e) {
                System.out.println("File not deleted : " + file);
            }
        }
        try {
            FileDeleteStrategy.FORCE.delete(dir);
        } catch (IOException e) {
            System.out.println("File not deleted : temp");
        }
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


}
