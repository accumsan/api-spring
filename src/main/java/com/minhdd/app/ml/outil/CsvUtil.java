package com.minhdd.app.ml.outil;

import com.minhdd.app.ml.service.kaggle.scs.FilesConstants;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.feature.StandardScalerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

import java.io.File;
import java.io.IOException;

/**
 * Created by minhdao on 06/03/16.
 */
public class CsvUtil {

    public static final String CSV_FORMAT = "com.databricks.spark.csv";

    public static DataFrame loadCsvFile(SQLContext sqlContext, String filePath, boolean header, boolean inferSchema) {
        if (filePath == null) return null;
        return sqlContext.read().format(CSV_FORMAT)
                .option("inferSchema", inferSchema ? "true" : "false")
                .option("header", header ? "true" : "false")
                .load(filePath);
    }

    public static DataFrame getDataFrameFromKaggleCsv(String filePath, SQLContext sqlContext, int offset, boolean scale) {
        DataFrame data = loadCsvFile(sqlContext, filePath, true, true);
        String[] columns = DataFrameUtil.getFeatureColumns(offset, data);
        if (scale) {
            VectorAssembler assembler = new VectorAssembler().setInputCols(columns).setOutputCol("assembledFeatures");
            StandardScalerModel scalerModel = StandardScalerModel.load(FilesConstants.SCALER);
            return scalerModel.transform(assembler.transform(data));
        } else {
            VectorAssembler assembler = new VectorAssembler().setInputCols(columns).setOutputCol("features");
            return assembler.transform(data);
        }
    }

    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDD(DataFrame df) {
        return df.toJavaRDD().map(row -> new LabeledPoint(row.getInt(0), row.getAs(1)));
    }

    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDDFromKaggleCsv(String filePath, SQLContext sqlContext, int offset, String labelColName) {
        return getLabeledPointJavaRDD(getDataFrameFromKaggleCsv(filePath, sqlContext, offset, false).select(labelColName, "features"));
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

}
