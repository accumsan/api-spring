package com.minhdd.app.ml.outil;

import com.minhdd.app.ml.service.kaggle.scs.FilesConstants;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.feature.PCAModel;
import org.apache.spark.ml.feature.StandardScalerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;

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

    public static DataFrame loadCsvFile(SQLContext sqlContext, String filePath, boolean header, StructType schema) {
        if (filePath == null) return null;
        return sqlContext.read().format(CSV_FORMAT)
                .schema(schema)
                .option("header", header ? "true" : "false")
                .load(filePath);
    }

    public static DataFrame getDataFrameFromCsv(String filePath, SQLContext sqlContext, int offset, boolean scale) {
        DataFrame data = loadCsvFile(sqlContext, filePath, true, true);
        return transformDataFrame(offset, scale, data);
    }

    public static DataFrame getDataFrameFromCsv(String filePath, SQLContext sqlContext, int offset, boolean scale, StructType schema) {
        DataFrame data = loadCsvFile(sqlContext, filePath, true, schema);
        return transformDataFrame(offset, scale, data);
    }

    private static DataFrame transformDataFrame(int offset, boolean scale, DataFrame data) {
        if (scale) {
            StandardScalerModel scalerModel = StandardScalerModel.load(FilesConstants.SCALER);
            return scalerModel.transform(DataFrameUtil.assembled(data, offset, "assembledFeatures"));
        } else {
            DataFrame df = DataFrameUtil.assembled(data, 2, "pca");
            PCAModel pcaModel = PCAModel.load(FilesConstants.PCA);
            return pcaModel.transform(df);
        }
    }

    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDD(DataFrame df) {
        return df.toJavaRDD().map(row -> new LabeledPoint(row.getInt(0), row.getAs(1)));
    }

    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDDFromKaggleCsv(String filePath, SQLContext sqlContext, int offset, String labelColName) {
        return getLabeledPointJavaRDD(getDataFrameFromCsv(filePath, sqlContext, offset, false).select(labelColName, "features"));
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
