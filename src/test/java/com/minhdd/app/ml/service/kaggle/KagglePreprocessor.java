package com.minhdd.app.ml.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.outil.CsvUtil;
import com.minhdd.app.ml.outil.DataFrameUtil;
import com.minhdd.app.ml.service.kaggle.scs.FilesConstants;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * Created by minhdao on 10/03/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
public class KagglePreprocessor {
    @Inject SQLContext sqlContext;

    @Test
    public void split_anomaly_detection() {
        DataFrame df = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_KAGGLE, true, true);
        DataFrame positives = df.filter("TARGET = 1");
        DataFrame[] positives_splits = positives.randomSplit(new double[]{0.5, 0.5});
        DataFrame positives_validation = positives_splits[0];
        DataFrame positives_test = positives_splits[1];
        DataFrame normals = df.filter("TARGET = 0");
        DataFrame[] splits = normals.randomSplit(new double[]{0.6, 0.4});
        DataFrame train = splits[0];
        DataFrame normals_40 = splits[1];
        DataFrame[] normal_splits = normals_40.randomSplit(new double[]{0.5, 0.5});
        DataFrame normals_20_validation = normal_splits[0];
        DataFrame normals_20_test = normal_splits[1];
        DataFrame validation = normals_20_validation.unionAll(positives_validation);
        DataFrame test = normals_20_test.unionAll(positives_test);
//        CsvUtil.save(train, FilesConstants.TRAIN_ANO, true);
//        CsvUtil.save(validation, FilesConstants.VALIDATION_ANO, true);
//        CsvUtil.save(test, FilesConstants.TEST_ANO, true);
    }


    @Test
    public void meanVector() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_MIN, true, true).drop("ID").drop("TARGET");
        DataFrame extract = data.randomSplit(new double[]{0.0025, 0.9975})[0];
        Vector mean = DataFrameUtil.mean(extract);
        System.out.println(mean);
    }

    @Test
    public void sigmaMatrix() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_MIN, true, true).drop("ID").drop("TARGET").select("var3", "var15", "var38");
        DataFrame extract = data.randomSplit(new double[]{0.0025, 0.9975})[0];
        Matrix m = DataFrameUtil.sigma(extract);
        System.out.println(m);
    }


}
