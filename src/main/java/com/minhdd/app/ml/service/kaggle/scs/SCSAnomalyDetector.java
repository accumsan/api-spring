package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import com.minhdd.app.ml.outil.CsvUtil;
import com.minhdd.app.ml.outil.DataFrameUtil;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.stat.distribution.MultivariateGaussian;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by minhdao on 16/03/16.
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class SCSAnomalyDetector extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(SCSAnomalyDetector.class);

    @Override
    public MLService loadData() {
        DataFrame train = CsvUtil.loadCsvFile(sqlContext, trainPath, true, true).drop("ID").drop("TARGET");
        DataFrame validation = CsvUtil.loadCsvFile(sqlContext, validationPath, true, true);
        DataFrame test = CsvUtil.loadCsvFile(sqlContext, testPath, true, true);
        return super.loadData(train, train, validation, test);
    }

    @Override
    protected MLAlgorithm<MultivariateGaussian, DataFrame> algorithm() {
        return (train -> {
            System.out.println("------------------");
            System.out.println("- begin training -");
            Vector mean = DataFrameUtil.mean(train);
            MultivariateGaussian multivariateGaussian = new MultivariateGaussian(mean, DataFrameUtil.sigma(train, mean));
            System.out.println("-  fin training  -");
            System.out.println("------------------");
            return multivariateGaussian;
        });
    }

    @Override
    public Map<String, Object> getResults() {
        System.out.println("-- begin printing results --");
        MultivariateGaussian multivariateGaussian = (MultivariateGaussian) model;
        DataFrame validation = (DataFrame) dataSet.getCrossValidation();
//        List<Tuple2<Double, Double>> predictionsAndLabelList = new ArrayList();
        long m = validation.count();
        String[] features = DataFrameUtil.getFeatureColumns(2, validation);
        List<Row> targets = validation.select("TARGET").collectAsList();
        for (long i=1; i<m; i++) {
            System.out.println("---" + i%5);
            double target = (double) targets.get((int) i).getInt(0);
            double prediction = multivariateGaussian.pdf(DataFrameUtil.extractVector(validation, features, i));
            if (prediction > 0.0) {
                System.out.println(prediction + " - " + target);
            }
//            predictionsAndLabelList.add(new Tuple2(prediction, target));
        }
        System.out.println("--  fin printing results  --");
        return null;
    }

}
