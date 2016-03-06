package com.minhdd.app.ml.service;

import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.domain.MLAlgorithm;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.domain.MlServiceAbstract;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by mdao on 04/03/2016.
 * http://spark.apache.org/docs/latest/ml-guide.html#example-estimator-transformer-and-param
 */
@Component
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class EstimatorTransformerParam extends MlServiceAbstract implements MLService {
    private final Logger logger = LoggerFactory.getLogger(EstimatorTransformerParam.class);

    @Override
    public MLService loadData() {
        DataFrame data = sqlContext.createDataFrame(Arrays.asList(
                new LabeledPoint(1.0, Vectors.dense(0.0, 1.1, 0.1)),
                new LabeledPoint(0.0, Vectors.dense(2.0, 1.0, -1.0)),
                new LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
                new LabeledPoint(1.0, Vectors.dense(0.0, 1.2, -0.5))
        ), LabeledPoint.class);
        return super.loadData(data);
    }

    @Override
    protected MLAlgorithm<LogisticRegressionModel> algorithm() {
        LogisticRegression lr = new LogisticRegression();
        lr.setMaxIter(conf.getMaxIteration()).setRegParam(conf.getRegParam());
        return (DataFrame training) -> lr.fit(training);
    }

    @Override
    public Map<String, Object> getResults() {
        LogisticRegressionModel logisticRegressionModel = (LogisticRegressionModel) model;
        // Prepare test documents.
        DataFrame test = sqlContext.createDataFrame(Arrays.asList(
                new LabeledPoint(1.0, Vectors.dense(-1.0, 1.5, 1.3)),
                new LabeledPoint(0.0, Vectors.dense(3.0, 2.0, -0.1)),
                new LabeledPoint(1.0, Vectors.dense(0.0, 2.2, -1.5))
        ), LabeledPoint.class);

        // Make predictions on test documents using the Transformer.transform() method.

        DataFrame results = logisticRegressionModel.transform(test);
        Map<String, Object> responses = new HashMap<>();
        List<String> stringResults = new ArrayList();
        for (Row r: results.select("features", "label", "probability", "prediction").collect()) {
            stringResults.add("(" + r.get(0) + ", " + r.get(1) + ") -> prob=" + r.get(2)
                    + ", prediction=" + r.get(3));
        }
        responses.put("results", stringResults);
        return responses;
    }
}
