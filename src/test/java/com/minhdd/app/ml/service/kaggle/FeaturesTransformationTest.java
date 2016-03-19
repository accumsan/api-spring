package com.minhdd.app.ml.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.outil.CsvUtil;
import com.minhdd.app.ml.outil.DataFrameUtil;
import com.minhdd.app.ml.service.kaggle.scs.FilesConstants;
import org.apache.spark.ml.feature.MinMaxScalerModel;
import org.apache.spark.ml.feature.StandardScaler;
import org.apache.spark.ml.feature.StandardScalerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by minhdao on 10/03/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
public class FeaturesTransformationTest {

    @Inject
    SQLContext sqlContext;

    //you have to remove first folder FilesConstants.SCALER
    @Test
    public void scaler() throws IOException {
        DataFrame train = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_KAGGLE, true, true).drop("TARGET").drop("ID");
        DataFrame test = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TEST_KAGGLE, true, true).drop("ID");
        DataFrame data = train.unionAll(test);
        String[] columns = DataFrameUtil.getFeatureColumns(0, data);
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(columns)
                .setOutputCol("assembledFeatures");

        DataFrame df = assembler.transform(data);

//        MinMaxScaler scaler = new MinMaxScaler().setMin(0).setMax(1)
//                .setInputCol("assembledFeatures")
//                .setOutputCol("features");
//
//        MinMaxScalerModel scalerModel = scaler.fit(df);
        StandardScaler scaler = new StandardScaler()
                .setInputCol("assembledFeatures")
                .setOutputCol("features")
                .setWithStd(true)
                .setWithMean(false);

        StandardScalerModel scalerModel = scaler.fit(df);
        scalerModel.save(FilesConstants.SCALER);
    }

    @Test
    public void applyScaler() {
        MinMaxScalerModel scalerModel = MinMaxScalerModel.load(FilesConstants.OUTPUT_DIR + "scaler.all.model");
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_MIN, true, true);
        String[] columns = DataFrameUtil.getFeatureColumns(2, data);
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(columns)
                .setOutputCol("assembledFeatures");

        DataFrame df = assembler.transform(data);
        DataFrame scaledData = scalerModel.transform(df);
        scaledData.show(false);
    }

    @Test
    public void featuresTest() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_MIN, true, true);
        System.out.println(data.columns().length);
        System.out.println(FilesConstants.EXCLUDED_COLUMNS.size());
        System.out.println(DataFrameUtil.getFeatureColumns(2, data).length);
    }
}
