package com.minhdd.app.ml.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.service.kaggle.scs.FilesConstants;
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
    public void split() {
        DataFrame df = CsvUtil.loadCsvFile(sqlContext, null, true, true);
        DataFrameUtil.splitToTwoDataSet(df, 0.25, null, null);
    }


    @Test
    public void normalize() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_MIN, true, true);
        String[] columns = CsvUtil.getFeatureColumns(2, data);
        //TODO
    }


}
