package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import com.minhdd.app.ml.service.kaggle.CsvUtil;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.mllib.stat.distribution.MultivariateGaussian;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.apache.spark.sql.functions.avg;

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
        return null;
    }

    @Override
    protected MLAlgorithm algorithm() {
        DataFrame train = (DataFrame) dataSet.getTraining();
        int m = train.columns().length;
        String[] columns = CsvUtil.getFeatureColumns(0, train);
        long n = train.count();


//        MultivariateGaussian multivariateGaussian = new MultivariateGaussian(train.first(), );
        return null;
    }


}
