package com.minhdd.app.ml.service.kaggle;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.outil.CsvUtil;
import com.minhdd.app.ml.outil.DataFrameUtil;
import com.minhdd.app.ml.service.kaggle.scs.FilesConstants;
import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by minhdao on 10/03/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
public class KagglePreprocessor {
    @Inject
    SQLContext sqlContext;

    @Test
    public void featuresFilter() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_KAGGLE, true, true);
        Row minDf = data.cube().min().first();
        Row maxDf = data.cube().max().first();
        String[] columns = data.columns();
        for (int i = 0; i < columns.length; i++) {
            if (minDf.get(i).equals(maxDf.get(i))) {
                System.out.println(columns[i]);
            }
        }
    }

    @Test
    public void rowDuplicationDetection() {
        DataFrame df = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_KAGGLE, true, true);
        String[] columns = DataFrameUtil.getFeatureColumns(2, df);
        System.out.println(df.count());
        DataFrame output = df.dropDuplicates(columns);
        System.out.println(output.count());
        CsvUtil.save(output, FilesConstants.OUTPUT_DIR + "train_deduplicated.csv", true);
    }

    @Test
    public void columnDuplicationDetection() {
        DataFrame df = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_KAGGLE, true, true);
        for (String column : FilesConstants.EXCLUDED_COLUMNS) {
            df = df.drop(column);
        }
        DataFrame mean = df.cube().avg();
        Map<Double, List<String>> duplicates = new HashMap();
        String[] columns = mean.columns();
        for (int i = 0; i < columns.length; i++) {
            String column = columns[i];
            Double key = Double.valueOf(mean.select(column).first().getDouble(0));
            if (duplicates.containsKey(key)) {
                duplicates.get(key).add(column);
            } else {
                List<String> list = new ArrayList<>();
                list.add(column);
                duplicates.put(key, list);
            }
        }
        for (Double key : duplicates.keySet()) {
            System.out.println(duplicates.get(key));
        }
    }

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
        CsvUtil.save(train, FilesConstants.TRAIN_ANO_DETECT, true);
        CsvUtil.save(validation, FilesConstants.VALIDATION_ANO_DETECT, true);
        CsvUtil.save(test, FilesConstants.TEST_ANO_DETECT, true);
    }


    @Test
    public void meanVector() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_MIN, true, true).drop("ID").drop("TARGET");
        DataFrame extract = data.randomSplit(new double[]{0.0025, 0.9975})[0];
        Vector mean = DataFrameUtil.mean(extract);
        System.out.println(mean);
    }

    @Test
    public void sigmaMatrixFromDataSet() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_MIN, true, true).drop("ID").drop("TARGET").select("var3", "var15", "var38");
        DataFrame extract = data.randomSplit(new double[]{0.0025, 0.9975})[0];
        Matrix m = DataFrameUtil.sigma(extract, DataFrameUtil.mean(extract));
        System.out.println(m);
    }

    /**
     * Using this matrix example
     * +---+---+
     * |  a|  b|
     * +---+---+
     * |  1|  2|
     * |  3|  8|
     * | 13| 21|
     * +---+---+
     */

    @Test
    public void sigmaMatrix() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, "data/dataframe/test1.csv", true, true);
        Matrix m = DataFrameUtil.sigma(data, DataFrameUtil.mean(data));
        Matrix expected = Matrices.dense(2, 2, new double[]{27.555555555555554, 41.11111111111111, 41.11111111111111, 62.88888888888889});
        System.out.println(expected);
        assertEquals(expected, m);
    }

    @Test
    public void getColumnsFromValue() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.VALIDATION_20, true, true);
        System.out.println(DataFrameUtil.getColumnsFromValue(data, -0.5));

    }


}
