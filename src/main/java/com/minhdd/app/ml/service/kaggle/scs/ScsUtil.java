package com.minhdd.app.ml.service.kaggle.scs;

import com.google.common.collect.ImmutableMap;
import com.minhdd.app.ml.outil.CsvUtil;
import com.minhdd.app.ml.outil.DataFrameUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.feature.*;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

import static org.apache.spark.sql.functions.lit;

/**
 * Created by mdao on 21/03/2016.
 */
public class ScsUtil {
    public static DataFrame getDataFrameFromCsv(SQLContext sqlContext, String filePath, String assembledColumnName, String pca) {
        return getDataFrameFromCsv(sqlContext, filePath, assembledColumnName, false, 1, pca);
    }

    public static DataFrame getDataFrameFromCsv(String filePath, SQLContext sqlContext) {
        return getDataFrameFromCsv(filePath, sqlContext, false);
    }

    public static DataFrame getDataFrameFromCsv(String filePath, SQLContext sqlContext, boolean scale) {
        return getDataFrameFromCsv(filePath, sqlContext, scale, 1);
    }

    public static DataFrame getDataFrameFromCsv(String filePath, SQLContext sqlContext, boolean scale, int polynomialExpansionDegree) {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, filePath, true, true);
        return transformDataFrame(data, scale, polynomialExpansionDegree);
    }

    public static DataFrame getDataFrameFromCsv(SQLContext sqlContext, String filePath, String assembledColumnName, boolean scale, int polynomialExpansionDegree, String pca) {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, filePath, true, true);
        return transformDataFrame(data, assembledColumnName, scale, polynomialExpansionDegree, pca);
    }

    private static DataFrame transformDataFrame(DataFrame data, boolean scale, int polynomialExpansionDegree) {
        return transformDataFrame(data, "features", scale, polynomialExpansionDegree, FilesConstants.PCA_261);
    }

    private static DataFrame transformDataFrame(DataFrame data, String finalColumnName, boolean scale, int polynomialExpansionDegree, String pca) {
        if (scale) {
            MinMaxScalerModel scalerModel = MinMaxScalerModel.load(FilesConstants.SCALER);
            return scalerModel.transform(DataFrameUtil.assembled(data, "assembledFeatures"));
        } else {
            DataFrame df = data.na().replace("var3", ImmutableMap.of(-999999, 2)).withColumn("n0", lit(0.0));
            df = DataFrameUtil.assembled(df, "pcain", DataFrameUtil.getFeatureColumns(df));
            System.out.println("PCA used : " + pca);
            PCAModel pcaModel = PCAModel.load(pca);
            DataFrame pcaOutput = pcaModel.transform(df);
            if (polynomialExpansionDegree > 1) {
                System.out.println("Polynomial expansion degree : " + polynomialExpansionDegree);
                PolynomialExpansion polyExpansion = new PolynomialExpansion()
                        .setInputCol("pcaout")
                        .setOutputCol(finalColumnName)
                        .setDegree(polynomialExpansionDegree);
                return polyExpansion.transform(pcaOutput);
            } else {
                return pcaOutput.withColumn(finalColumnName, pcaOutput.col("pcaout"));
            }
        }
    }

    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDD(DataFrame df) {
        return df.toJavaRDD().map(row -> new LabeledPoint(row.getInt(0), row.getAs(1)));
    }

    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDDFromCsv(String filePath, SQLContext sqlContext, String labelColName, boolean scale) {
        return getLabeledPointJavaRDD(getDataFrameFromCsv(filePath, sqlContext, scale, 1).select(labelColName, "features"));
    }

    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDDFromCsv(String filePath, SQLContext sqlContext, String labelColName) {
        return getLabeledPointJavaRDDFromCsv(filePath, sqlContext, labelColName, false);
    }

    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDDFromCsv(String filePath, SQLContext sqlContext) {
        return getLabeledPointJavaRDDFromCsv(filePath, sqlContext, "TARGET");
    }
}
