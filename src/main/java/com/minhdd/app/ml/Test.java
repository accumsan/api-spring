//package com.minhdd.app.ml;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.ml.classification.LogisticRegressionModel;
//import org.apache.spark.ml.param.ParamMap;
//import org.apache.spark.ml.classification.LogisticRegression;
//import org.apache.spark.mllib.linalg.Vectors;
//import org.apache.spark.mllib.regression.LabeledPoint;
//import org.apache.spark.sql.DataFrame;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.SQLContext;
//
///**
// * Created by mdao on 02/03/2016.
// */
//public class Test {
//
//    public static void mainn(String[] args) {
//        SparkConf conf = new SparkConf().setAppName("JavaSimpleLinearRegression").setMaster("local");
//        JavaSparkContext jsc = new JavaSparkContext(conf);
//        SQLContext sqlContext = new SQLContext(jsc);
//        DataFrame training = sqlContext.createDataFrame(Arrays.asList(
//                new LabeledPoint(1.0, Vectors.dense(0.0, 1.1, 0.1)),
//                new LabeledPoint(0.0, Vectors.dense(2.0, 1.0, -1.0)),
//                new LabeledPoint(0.0, Vectors.dense(2.0, 1.3, 1.0)),
//                new LabeledPoint(1.0, Vectors.dense(0.0, 1.2, -0.5))
//        ), LabeledPoint.class);
//
//        LogisticRegression lr = new LogisticRegression();
//        System.out.println("LogisticRegression parameters:\n" + lr.explainParams() + "\n");
//
//        lr.setMaxIter(10)
//                .setRegParam(0.01);
//
//        LogisticRegressionModel model1 = lr.fit(training);
//        System.out.println("Model 1 was fit using parameters: " + model1.parent().extractParamMap());
//
//        ParamMap paramMap = new ParamMap()
//                .put(lr.maxIter().w(20)) // Specify 1 Param.
//                .put(lr.maxIter(), 30) // This overwrites the original maxIter.
//                .put(lr.regParam().w(0.1), lr.threshold().w(0.55)); // Specify multiple Params.
//
//        ParamMap paramMap2 = new ParamMap()
//                .put(lr.probabilityCol().w("myProbability")); // Change output column name
//        ParamMap paramMapCombined = paramMap.$plus$plus(paramMap2);
//
//        LogisticRegressionModel model2 = lr.fit(training, paramMapCombined);
//        System.out.println("Model 2 was fit using parameters: " + model2.parent().extractParamMap());
//
//        DataFrame test = sqlContext.createDataFrame(Arrays.asList(
//                new LabeledPoint(1.0, Vectors.dense(-1.0, 1.5, 1.3)),
//                new LabeledPoint(0.0, Vectors.dense(3.0, 2.0, -0.1)),
//                new LabeledPoint(1.0, Vectors.dense(0.0, 2.2, -1.5))
//        ), LabeledPoint.class);
//
//        DataFrame results = model2.transform(test);
//        for (Row r : results.select("features", "label", "myProbability", "prediction").collect()) {
//            System.out.println("(" + r.get(0) + ", " + r.get(1) + ") -> prob=" + r.get(2)
//                    + ", prediction=" + r.get(3));
//        }
//    }
//}
