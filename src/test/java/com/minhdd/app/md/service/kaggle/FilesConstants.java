package com.minhdd.app.md.service.kaggle;

/**
 * Created by minhdao on 10/03/16.
 */
public class FilesConstants {
    public final static String LOCAL_DIR_QS = "/Users/mdao/ws/minh/ml/kaggle/santander-customer-satisfaction/";
    public final static String LOCAL_DIR_MAC86 = "/Users/minhdao/Workspace/ml/kaggle/santander-customer-satisfaction/data/";
    public final static String TRAIN_SAMPLE = "data/kaggle/santander-customer-satisfaction/train-1.csv";
    public final static String TEST_SAMPLE = "data/kaggle/santander-customer-satisfaction/test-1.csv";
    public final static String TRAIN_KAGGLE = LOCAL_DIR_MAC86 + "train.csv";
    public final static String TEST_KAGGLE = LOCAL_DIR_MAC86 + "test.csv";
    public final static String OUTPUT_DIR = "data/kaggle/santander-customer-satisfaction/save/";
    public final static String RFP_MODEL = OUTPUT_DIR + "random-forest-pipeline.model";
    public final static String GBT_MODEL = OUTPUT_DIR + "gradient-boosted-pipeline.model";
    public final static String LR_MODEL = OUTPUT_DIR + "logistic-regression.model";
    public final static String BinaryClassification_MODEL = OUTPUT_DIR + "binary-classification.model";
    public final static String TEST_OUTPUT = OUTPUT_DIR + "predictions.csv";
    public static final String TRAIN_SPLIT = LOCAL_DIR_MAC86 + "train_split";
    public static final String TEST_SPLIT = LOCAL_DIR_MAC86 + "test_split";
}
