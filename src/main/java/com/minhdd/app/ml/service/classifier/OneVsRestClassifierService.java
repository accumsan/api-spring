package com.minhdd.app.ml.service.classifier;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.classification.OneVsRest;
import org.apache.spark.ml.classification.OneVsRestModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.util.MetadataUtils;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 * http://spark.apache.org/docs/latest/ml-classification-regression.html#one-vs-rest-classifier-aka-one-vs-all
 * Feedforward neural network
 */
public class OneVsRestClassifierService extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(OneVsRestClassifierService.class);

    @Override
    public MLService loadData() {
        DataFrame data = loadFile();
        double f = conf.getFractionTest();
        DataFrame[] splits = data.randomSplit(new double[]{1 - f, f}, 12345);
        DataFrame trainingData = splits[0];
        DataFrame testData = splits[1];
        return super.loadData(data, trainingData, null, testData);
    }

    @Override
    protected MLAlgorithm<OneVsRestModel, DataFrame> algorithm() {
        LogisticRegression classifier = new LogisticRegression()
                .setMaxIter(conf.getMaxIteration())
                .setTol(conf.getTol())
                .setFitIntercept(true)
                .setRegParam(conf.getRegParam())
                .setElasticNetParam(conf.getElasticNetParam());
        OneVsRest ovr = new OneVsRest().setClassifier(classifier);

        return (DataFrame training) -> ovr.fit(training);
    }

    @Override
    public MLService test() {
        predictions = ((OneVsRestModel)model).transform((DataFrame)dataSet.getTest());
        return super.test();
    }

    @Override
    public Map<String, Object> getResults() {
        DataFrame predictions = (DataFrame) this.predictions;
        DataFrame predictionAndLabels = predictions.select("prediction", "label");
        MulticlassMetrics metrics = new MulticlassMetrics(predictionAndLabels);
        StructField predictionColSchema = predictionAndLabels.schema().apply("prediction");
        Integer numClasses = (Integer) MetadataUtils.getNumClasses(predictionColSchema).get();

        // compute the false positive rate per label
        StringBuilder results = new StringBuilder();
        results.append("label\tfpr\n");
        for (int label = 0; label < numClasses; label++) {
            results.append(label);
            results.append("\t");
            results.append(metrics.falsePositiveRate((double) label));
            results.append("\n");
        }

        Matrix confusionMatrix = metrics.confusionMatrix();
        // output the Confusion Matrix
        System.out.println("Confusion Matrix");
        System.out.println(confusionMatrix);
        System.out.println();
        System.out.println(results);
        Map<String, Object> responses = new HashMap<>();
        responses.put("results", results.toString());
        return responses;
    }
}
