package com.minhdd.app.md.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.service.kaggle.CsvUtil;
import com.minhdd.app.ml.service.kaggle.DataFrameUtil;
import org.apache.spark.SparkContext;
import org.apache.spark.ml.feature.Normalizer;
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
    public void normalized() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_SAMPLE, true, true);
        data.show();
        String[] columns = data.columns();
        for (String column : columns) {
            if (!column.equals("TARGET") && !column.equals("ID")) {
                //System.out.println()
            }
        }
        data.show();
    }

    @Test
    public void split() {
        DataFrame df = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_SAMPLE, true, true);
        DataFrameUtil.splitToTrainAndCrossValidation(df, 0.2, FilesConstants.TRAIN_SPLIT, FilesConstants.TEST_SPLIT);
    }

}
