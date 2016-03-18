package com.minhdd.app.ml.service.kaggle.scs;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import com.minhdd.app.ml.outil.CsvUtil;
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.types.DataTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by minhdao on 14/03/16.
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class SCSPerceptronClassifier extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(SCSPerceptronClassifier.class);

    @Override
    public MLService loadData() {
        DataFrame train = CsvUtil.getDataFrameFromCsv(trainPath, sqlContext, 2, false).select("ID", "features", "TARGET");
        train = train.withColumn("label", train.col("TARGET").cast(DataTypes.DoubleType));
        DataFrame validation = CsvUtil.getDataFrameFromCsv(validationPath, sqlContext, 2, false).select("ID", "features", "TARGET");
        validation = validation.withColumn("label", validation.col("TARGET").cast(DataTypes.DoubleType));
        DataFrame test = CsvUtil.getDataFrameFromCsv(testPath, sqlContext, 2, false).select("ID", "features", "TARGET");
        test = test.withColumn("label", test.col("TARGET").cast(DataTypes.DoubleType));
        return super.loadData(train, train, validation, test);
    }

    @Override
    protected MLAlgorithm<MultilayerPerceptronClassificationModel, DataFrame> algorithm() {
        MultilayerPerceptronClassifier trainer = new MultilayerPerceptronClassifier()
                .setLayers(conf.getNn().getLayers())
                .setBlockSize(conf.getNn().getBlockSize())
                .setSeed(conf.getNn().getSeed())
                .setMaxIter(conf.getMaxIteration());

        return (DataFrame training) -> trainer.fit(training);
    }

    @Override
    public MLService test() {
        predictions = ((MultilayerPerceptronClassificationModel) model).transform((DataFrame) dataSet.getTest());
        return super.test();
    }

    @Override
    public Map<String, Object> getResults() {
        System.out.println("================================================");
        DataFrame predictions = (DataFrame) this.predictions;
        predictions.show();
//        DataFrame predictionAndLabels = predictions.select("prediction", "label");
//        predictionAndLabels.show();
//        System.out.println("Number of predictions : " + predictionAndLabels.count());
//        System.out.println("Number of label 1 : " + predictionAndLabels.filter("label = 1").count());
//        System.out.println("Number of predicted 1 : " + predictions.filter("prediction = 1.0").count());
//        System.out.println("Good predictions for label 1 : " + predictions.filter("label = 1").filter("prediction = 1.0").count());
//        System.out.println("Bad predictions (to 1) of target 0 : " + predictions.filter("label = 0").filter("prediction = 1.0").count());
        return null;
    }
}