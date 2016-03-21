package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.ml.outil.CsvUtil;
import com.minhdd.app.ml.outil.DataFrameUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.feature.MinMaxScalerModel;
import org.apache.spark.ml.feature.PCAModel;
import org.apache.spark.ml.feature.PolynomialExpansion;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

/**
 * Created by mdao on 21/03/2016.
 */
public class ScsUtil {
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

    private static DataFrame transformDataFrame(DataFrame data, boolean scale, int polynomialExpansionDegree) {
        if (scale) {
            MinMaxScalerModel scalerModel = MinMaxScalerModel.load(FilesConstants.SCALER);
            return scalerModel.transform(DataFrameUtil.assembled(data, "assembledFeatures"));
        } else {
            DataFrame df = DataFrameUtil.assembled(data, "pcain");
            PCAModel pcaModel = PCAModel.load(FilesConstants.PCA);
            DataFrame pcaOutput = pcaModel.transform(df);
            if (polynomialExpansionDegree > 1) {
                System.out.println("Polynomial expansion degree : " + polynomialExpansionDegree);
                PolynomialExpansion polyExpansion = new PolynomialExpansion()
                        .setInputCol("pcaout")
                        .setOutputCol("features")
                        .setDegree(polynomialExpansionDegree);
                return polyExpansion.transform(pcaOutput);
            } else {
                return pcaOutput.withColumn("features", pcaOutput.col("pcaout"));
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
