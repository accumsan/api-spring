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
    public Map<String, Object> getResults() {
        MultivariateGaussian multivariateGaussian = (MultivariateGaussian) model;
        DataFrame validation = (DataFrame) dataSet.getCrossValidation();
        List<Tuple2<Double, Double>> predictionsAndLabelList = new ArrayList();
        long m = validation.count();
        for (long i=1; i<m; i++) {
            Vector x = DataFrameUtil.extractVector(validation, DataFrameUtil.getFeatureColumns(2, validation), i);
            double target = (double) validation.select("TARGET").collectAsList().get((int) i).getInt(0);
            double prediction = multivariateGaussian.pdf(x);
            if (prediction > 0.0) {
                System.out.println(prediction + " - " + target);
            }
            Tuple2<Double, Double> t = new Tuple2(multivariateGaussian.pdf(x), target);
            predictionsAndLabelList.add(t);
        }
        return null;
    }

    @Override
    protected MLAlgorithm<MultivariateGaussian, DataFrame> algorithm() {
        DataFrame train = (DataFrame) dataSet.getTraining();
        MultivariateGaussian multivariateGaussian = new MultivariateGaussian(DataFrameUtil.mean(train), DataFrameUtil.sigma(train));
        return (training -> multivariateGaussian);
    }


}
