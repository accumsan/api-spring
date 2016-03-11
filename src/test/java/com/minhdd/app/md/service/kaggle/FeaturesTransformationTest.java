package com.minhdd.app.md.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.service.kaggle.CsvUtil;
import org.apache.spark.SparkContext;
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
public class FeaturesTransformationTest {
    private final String LOCAL_DIR_QS = "/Users/mdao/ws/minh/ml/kaggle/santander-customer-satisfaction/";
    private final String LOCAL_DIR_MAC86 = "/Users/minhdao/Workspace/ml/kaggle/santander-customer-satisfaction/data/";
    private final String TRAIN_SAMPLE = "data/kaggle/santander-customer-satisfaction/train-1.csv";
    private final String TEST_SAMPLE = "data/kaggle/santander-customer-satisfaction/test-1.csv";
    private final String TRAIN_KAGGLE = LOCAL_DIR_MAC86 + "train.csv";
    private final String TEST_KAGGLE = LOCAL_DIR_MAC86 + "test.csv";

    @Inject SQLContext sqlContext;

    @Inject SparkContext sparkContext;

    @Test
    public void test1() {
        System.out.println();
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, TEST_KAGGLE, true, true);
        System.out.println(CsvUtil.getColumnsFromValue(data, 113478.9));
    }
}
