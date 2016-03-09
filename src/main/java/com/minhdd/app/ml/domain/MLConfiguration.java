package com.minhdd.app.ml.domain;

/**
 * Created by minhdao on 05/03/16.
 */
public class MLConfiguration {
    public int getMaxIteration() {
        return maxIteration;
    }

    public MLConfiguration setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
        return this;
    }

    public double getElasticNetParam() {
        return elasticNetParam;
    }

    public MLConfiguration setElasticNetParam(double elasticNetParam) {
        this.elasticNetParam = elasticNetParam;
        return this;
    }

    public double getRegParam() {
        return regParam;
    }

    public MLConfiguration setRegParam(double regParam) {
        this.regParam = regParam;
        return this;
    }

    public double getFractionTest() {
        return fractionTest;
    }

    public MLConfiguration setFractionTest(double fracTest) {
        this.fractionTest = fracTest;
        return this;
    }

    public MLConfiguration setTol(double tol) {
        this.tol = tol;
        return this;
    }

    public double getTol() {
        return tol;
    }

    public NeuralNetworkConfiguration getNn() {
        return neuralNetworkConfiguration;
    }

    public MLConfiguration setNeuralNetworkConfiguration(NeuralNetworkConfiguration neuralNetworkConfiguration) {
        this.neuralNetworkConfiguration = neuralNetworkConfiguration;
        return this;
    }

    private NeuralNetworkConfiguration neuralNetworkConfiguration;

    public MLConfiguration setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }
    public String getAlgorithm() {
        return algorithm;
    }

    private String algorithm;
    private int maxIteration;
    private double regParam;
    private double elasticNetParam;
    private double fractionTest;
    private double tol;


}
