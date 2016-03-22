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
import java.util.*;

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
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_ORIGINAL_KAGGLE, true, true);
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
        DataFrame df = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_ORIGINAL_KAGGLE, true, true);
        String[] columns = DataFrameUtil.getFeatureColumns(df);
        System.out.println(df.count());
        DataFrame output = df.dropDuplicates(columns);
        System.out.println(output.count());
        CsvUtil.save(output, FilesConstants.OUTPUT_DIR + "train_deduplicated.csv", true);
    }

    @Test
    public void columnDuplicationOrProportionalDetection() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_ORIGINAL_KAGGLE, true, true);
        String[] features = DataFrameUtil.getFeatureColumns(data);
        for (String column : FilesConstants.EXCLUDED_COLUMNS) {
            data = data.drop(column);
        }
        final DataFrame df = data;
        String[] columns = df.columns();
        Row meanRow = df.cube().avg().first();
        Row maxRow = df.cube().max().first();
        double[] result = new double[columns.length];
        for (int i = 0; i < columns.length; i++) {
            try {
                result[i] = meanRow.getDouble(i) / maxRow.getInt(i);
            } catch(ClassCastException e) {
                try {
                    result[i] = meanRow.getDouble(i) / maxRow.getLong(i);
                } catch(ClassCastException e2) {
                    result[i] = meanRow.getDouble(i) / maxRow.getDouble(i);
                }
            }
        }
        Map<Long, List<String>> duplicates = new HashMap();
        for (int i = 0; i < columns.length; i++) {
            String column = columns[i];
            Long key = Long.valueOf((int) (1000000000 * result[i]));
            if (duplicates.containsKey(key)) {
                duplicates.get(key).add(column);
            } else {
                List<String> list = new ArrayList<>();
                list.add(column);
                duplicates.put(key, list);
            }
        }
        List<List<String>> verified = new ArrayList<>();
        List<List<String>> toVerify = new ArrayList<>();
//        duplicates.keySet().stream().sorted().filter(key -> duplicates.get(key).size() == 2).forEach(key -> {
//            boolean verify = DataFrameUtil.sameRatio(df, duplicates.get(key).get(0), duplicates.get(key).get(1));
//            if (verify) verified.add(duplicates.get(key));
//            else toVerify.add(duplicates.get(key));
//        });
//        duplicates.keySet().stream().sorted().filter(key -> duplicates.get(key).size() == 3 ).forEach(key -> {
//            System.out.println(duplicates.get(key));
//            for (int i = 0; i<duplicates.get(key).size()-1; i++) {
//                for (int j = i+1; j<duplicates.get(key).size(); j++) {
//                    boolean verify = DataFrameUtil.sameRatio(df, duplicates.get(key).get(i), duplicates.get(key).get(j));
//                    if (verify) verified.add(duplicates.get(key));
//                    else toVerify.add(duplicates.get(key));
//                }
//            }
//        });
        duplicates.keySet().stream().sorted().filter(key -> duplicates.get(key).size() > 3 ).forEach(key -> {
            System.out.println(duplicates.get(key));
            for (int i = 0; i<duplicates.get(key).size()-1; i++) {
                boolean verify = DataFrameUtil.sameRatio(df, duplicates.get(key).get(i), duplicates.get(key).get(i+1));
                if (verify) verified.add(duplicates.get(key));
                else toVerify.add(duplicates.get(key));
            }
        });
    }

    @Test
    public void compareTwoColumns() {
        DataFrame df = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_ORIGINAL_KAGGLE, true, true);
        DataFrameUtil.sameRatio(df, "delta_imp_aport_var33_1y3", "delta_imp_trasp_var33_out_1y3");
    }

    @Test
    public void split() {
        DataFrame df = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_DEDUPLICATED_KAGGLE, true, true);
        DataFrame[] splits = df.randomSplit(new double[]{0.5, 0.5});
        DataFrame train_50 = splits[0];
        DataFrame validation_test = splits[1];
        CsvUtil.save(train_50, FilesConstants.TRAIN_50, true);
        DataFrame[] splits2 = validation_test.randomSplit(new double[]{0.8, 0.2});
        DataFrame validation_40 = splits2[0];
        DataFrame test_10 = splits2[1];
        CsvUtil.save(validation_40, FilesConstants.VALIDATION_40, true);
        CsvUtil.save(test_10, FilesConstants.TEST_10, true);
    }

    @Test
    public void split_anomaly_detection() {
        DataFrame df = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_DEDUPLICATED_KAGGLE, true, true);
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

    @Test
    public void verifyTargetRatio() {
        DataFrameUtil.verifyTargetRatio(sqlContext, FilesConstants.TRAIN_50, "train50");
        DataFrameUtil.verifyTargetRatio(sqlContext, FilesConstants.TRAIN_60, "train60");
        DataFrameUtil.verifyTargetRatio(sqlContext, FilesConstants.VALIDATION_40, "validation40");
        DataFrameUtil.verifyTargetRatio(sqlContext, FilesConstants.VALIDATION_20, "validation20");
        DataFrameUtil.verifyTargetRatio(sqlContext, FilesConstants.TEST_20, "test20");
        DataFrameUtil.verifyTargetRatio(sqlContext, FilesConstants.TEST_10, "test10");
    }


}
