package com.minhdd.app.ml.domain;

/**
 * Created by minhdao on 05/03/16.
 */
public class NeuralNetworkConfiguration {
    private int[] layers;
    private int blockSize;
    private Long seed;

    public int getBlockSize() {
        return blockSize;
    }

    public NeuralNetworkConfiguration setBlockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    public Long getSeed() {
        return seed;
    }

    public NeuralNetworkConfiguration setSeed(Long seed) {
        this.seed = seed;
        return this;
    }

    public int[] getLayers() {
        return layers;
    }

    public NeuralNetworkConfiguration setLayers(int[] layers) {
        this.layers = layers;
        return this;
    }

}
