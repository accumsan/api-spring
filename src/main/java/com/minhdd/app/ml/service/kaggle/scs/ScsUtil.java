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
import org.apache.spark.sql.types.StructType;

/**
 * Created by mdao on 21/03/2016.
 */
public class ScsUtil {
    public static DataFrame getDataFrameFromCsv(String filePath, SQLContext sqlContext, boolean scale) {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, filePath, true, true);
        return transformDataFrame(scale, data);
    }

    public static DataFrame getDataFrameFromCsv(String filePath, SQLContext sqlContext, boolean scale, StructType schema) {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, filePath, true, schema);
        return transformDataFrame(scale, data);
    }
    private static DataFrame transformDataFrame(boolean scale, DataFrame data) {
        if (scale) {
            MinMaxScalerModel scalerModel = MinMaxScalerModel.load(FilesConstants.SCALER);
            return scalerModel.transform(DataFrameUtil.assembled(data, "assembledFeatures"));
        } else {
            DataFrame df = DataFrameUtil.assembled(data, "pcain");
            PCAModel pcaModel = PCAModel.load(FilesConstants.PCA);
            PolynomialExpansion polyExpansion = new PolynomialExpansion()
                    .setInputCol("pcaout")
                    .setOutputCol("features")
                    .setDegree(2);
            return polyExpansion.transform(pcaModel.transform(df));
        }
    }
    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDD(DataFrame df) {
        return df.toJavaRDD().map(row -> new LabeledPoint(row.getInt(0), row.getAs(1)));
    }

    public static JavaRDD<LabeledPoint> getLabeledPointJavaRDDFromKaggleCsv(String filePath, SQLContext sqlContext, String labelColName, boolean scale) {
        return getLabeledPointJavaRDD(getDataFrameFromCsv(filePath, sqlContext, scale).select(labelColName, "features"));
    }
}
