package com.minhdd.app.ml.service.kaggle;

import org.apache.spark.ml.feature.RFormula;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

/**
 * Created by minhdao on 06/03/16.
 */
public class CsvUtil {
    private static DataFrame loadCsvFile(SQLContext sqlContext, String filePath, boolean header, boolean inferSchema) {
        return  sqlContext.read().format("com.databricks.spark.csv")
                .option("inferSchema", inferSchema ? "true" : "false")
                .option("header", header ? "true" : "false")
                .load(filePath);
    }

    public static DataFrame getDataFrameFromKaggleCsv(String filePath, SQLContext sqlContext, int offset) {
        DataFrame data = loadCsvFile(sqlContext, filePath, true, true);

        //features columns
        String[] columns = new String[data.columns().length-offset];
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
}
