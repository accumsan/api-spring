package com.minhdd.app.ml.service.kaggle;

import org.apache.spark.ml.feature.RFormula;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by minhdao on 06/03/16.
 */
public class CsvUtil {

    public static final String CSV_FORMAT = "com.databricks.spark.csv";

    private static DataFrame loadCsvFile(SQLContext sqlContext, String filePath, boolean header, boolean inferSchema) {
        return sqlContext.read().format(CSV_FORMAT)
                .option("inferSchema", inferSchema ? "true" : "false")
                .option("header", header ? "true" : "false")
                .load(filePath);
    }

    public static DataFrame getDataFrameFromKaggleCsv(String filePath, SQLContext sqlContext, int offset) {
        DataFrame data = loadCsvFile(sqlContext, filePath, true, true);

        //features columns
        String[] columns = new String[data.columns().length - offset];
        int i = 0;
        for (String column : data.columns()) {
            if (!column.equals("TARGET") && !column.equals("ID")) {
                columns[i++] = column;
            }
        }

        //prepare Formula for RFormula
        StringBuilder featuresFormula = new StringBuilder();
        for (String column : columns) {
            featuresFormula.append(" + ");
            featuresFormula.append(column);
        }
        featuresFormula.setCharAt(1, '~');
        //System.out.println("TARGET" + featuresFormula.toString());

        //http://spark.apache.org/docs/latest/ml-features.html#rformula
        RFormula rFormula = new RFormula()
                .setFormula("TARGET" + featuresFormula.toString())
                .setFeaturesCol("features")
                .setLabelCol("label");

        return rFormula.fit(data).transform(data);
    }

    public static void save(DataFrame predictions, String output, boolean header) {
        new File("temp").delete();
        new File(output).delete();
        predictions.repartition(1).write().format(CSV_FORMAT)
                .option("header", header ? "true" : "false")
                .option("delimiter", ",")
                .save("temp");
        File dir = new File("temp");
        (new File("temp/part-00000")).renameTo(new File(output));
        for (File file : dir.listFiles()) {
            file.delete();
        }
        dir.delete();
    }
}
