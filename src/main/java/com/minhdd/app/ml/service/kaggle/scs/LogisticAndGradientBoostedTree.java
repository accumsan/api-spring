package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.DataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.util.Map;

/**
 * Created by minhdao on 21/03/16.
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class LogisticAndGradientBoostedTree extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(LogisticAndGradientBoostedTree.class);

    @Override
    public MLService loadData() {
        return null;
    }

    @Override
    protected MLAlgorithm algorithm() {
        DataFrame train = ScsUtil.getDataFrameFromCsv(trainPath, sqlContext).select("TARGET", "features", "ID");
        DataFrame validation = ScsUtil.getDataFrameFromCsv(validationPath, sqlContext).select("TARGET", "features", "ID");
        DataFrame test = ScsUtil.getDataFrameFromCsv(testPath, sqlContext).select("TARGET", "features", "ID");
        JavaRDD<LabeledPoint> trainRDD = ScsUtil.getLabeledPointJavaRDDFromCsv(trainPath, sqlContext, "TARGET");
        JavaRDD<LabeledPoint> validationRDD = ScsUtil.getLabeledPointJavaRDDFromCsv(validationPath, sqlContext, "TARGET");
        JavaRDD<LabeledPoint> testRDD = ScsUtil.getLabeledPointJavaRDDFromCsv(testPath, sqlContext, "TARGET");

        final LogisticRegressionWithLBFGS model = new LogisticRegressionWithLBFGS().setNumClasses(2);
        LogisticRegressionModel lrm = model.run(trainRDD.rdd());
        lrm.clearThreshold();
        JavaRDD<Tuple2<Object, Object>> predictionAndLabels = validationRDD.map(p -> new Tuple2<>(lrm.predict(p.features()), p.label()));

        return null;
    }

    @Override
    public Map<String, Object> getResults() {
        return null;
    }
}
