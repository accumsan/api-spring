package com.minhdd.app.ml.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.service.kaggle.scs.FilesConstants;
import org.apache.spark.ml.feature.MinMaxScaler;
import org.apache.spark.ml.feature.MinMaxScalerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.io.IOException;

import static org.apache.spark.sql.functions.lit;

/**
 * Created by minhdao on 10/03/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
public class FeaturesTransformationTest {

    @Inject
    SQLContext sqlContext;

    @Test
    public void scaler() throws IOException {
        DataFrame train = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_KAGGLE, true, true);
        DataFrame test = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TEST_KAGGLE, true, true).withColumn("TARGET", lit(0.0));
        DataFrame data = train.unionAll(test);
        String[] columns = CsvUtil.getFeatureColumns(2, data);
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(columns)
                .setOutputCol("assembledFeatures");

        DataFrame df = assembler.transform(data);

        MinMaxScaler scaler = new MinMaxScaler().setMin(-0.5).setMax(0.5)
                .setInputCol("assembledFeatures")
                .setOutputCol("features");

        MinMaxScalerModel scalerModel = scaler.fit(df);
        scalerModel.save(FilesConstants.OUTPUT_DIR + "scaler.all.model");
    }

    @Test
    public void applyScaler() {
        MinMaxScalerModel scalerModel = MinMaxScalerModel.load(FilesConstants.OUTPUT_DIR + "scaler.all.model");
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_MIN, true, true);
        String[] columns = CsvUtil.getFeatureColumns(2, data);
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(columns)
                .setOutputCol("assembledFeatures");

        DataFrame df = assembler.transform(data);
        DataFrame scaledData = scalerModel.transform(df);
        scaledData.show(false);
    }
}
