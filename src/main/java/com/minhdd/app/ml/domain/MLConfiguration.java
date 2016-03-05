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

    public NeuralNetworkConfiguration getNn() {
        return neuralNetworkConfiguration;
    }

    public MLConfiguration setNeuralNetworkConfiguration(NeuralNetworkConfiguration neuralNetworkConfiguration) {
        this.neuralNetworkConfiguration = neuralNetworkConfiguration;
        return this;
    }

    private NeuralNetworkConfiguration neuralNetworkConfiguration;
    private int maxIteration;
    private double regParam;
    private double elasticNetParam;
}
