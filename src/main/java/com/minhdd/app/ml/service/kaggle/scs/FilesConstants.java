package com.minhdd.app.ml.service.kaggle.scs;

import java.util.Arrays;
import java.util.List;

/**
 * Created by minhdao on 10/03/16.
 */
public class FilesConstants {
    public final static String TRAIN_MIN = "data/kaggle/santander-customer-satisfaction/train_min.csv";
    public final static String VALIDATION_MIN = "data/kaggle/santander-customer-satisfaction/validation_min.csv";
    public final static String TEST_MIN = "data/kaggle/santander-customer-satisfaction/test_min.csv";
    public final static String POSITIVES_MIN = "data/kaggle/santander-customer-satisfaction/positives_min.csv";
    public final static String POSITIVES_20 = "data/kaggle/santander-customer-satisfaction/positives_20.csv";

    public final static String OUTPUT_DIR = "data/kaggle/santander-customer-satisfaction/save/";
    public final static String SCALER = OUTPUT_DIR + "scaler.all.model";
    public final static String PCA = OUTPUT_DIR + "pca.model";
    public final static String CHISQ = OUTPUT_DIR + "chisq.model";
    public final static String RFP_MODEL = OUTPUT_DIR + "random-forest-pipeline.model";
    public final static String GBT_MODEL = OUTPUT_DIR + "gradient-boosted-pipeline.model";
    public final static String LR_MODEL = OUTPUT_DIR + "logistic-regression.model";
    public final static String BinaryClassification_MODEL = OUTPUT_DIR + "binary-classification.model";
    public final static String TEST_OUTPUT = OUTPUT_DIR + "predictions.csv";

    public final static String LOCAL_DIR_QS = "/Users/mdao/ws/minh/ml/kaggle/santander-customer-satisfaction/";
    public final static String LOCAL_DIR_MAC86 = "/Users/minhdao/Workspace/ml/kaggle/santander-customer-satisfaction/data/";
    public final static String LOCAL_DIR = LOCAL_DIR_MAC86;
    public final static String LOCAL_DIR_EXTRACT = LOCAL_DIR + "data-extract/";
    public final static String LOCAL_DIR_ANO_DETECT = LOCAL_DIR + "ano-detect/";
    public final static String TRAIN_KAGGLE = LOCAL_DIR + "train.csv";
    public final static String TEST_KAGGLE = LOCAL_DIR + "test.csv";
    public static final String TRAIN_80 = LOCAL_DIR_EXTRACT + "train_80.csv";
    public static final String TRAIN_60 = LOCAL_DIR_EXTRACT + "train_60.csv";
    public static final String VALIDATION_20 = LOCAL_DIR_EXTRACT + "validation_20.csv";
    public static final String TEST_20 = LOCAL_DIR_EXTRACT + "test_20.csv";
    public static final String TRAIN_ANO_DETECT = LOCAL_DIR_ANO_DETECT + "ano_detect_train.csv";
    public static final String VALIDATION_ANO_DETECT = LOCAL_DIR_ANO_DETECT + "ano_detect_validation.csv";
    public static final String TEST_ANO_DETECT = LOCAL_DIR_ANO_DETECT + "ano_detect_test.csv";

    public static final List<String> EXCLUDED_COLUMNS = Arrays.asList("ind_var2_0", "ind_var2", "ind_var27_0", "ind_var28_0", "ind_var28", "ind_var27", "ind_var41", "ind_var46_0", "ind_var46", "num_var27_0", "num_var28_0", "num_var28", "num_var27", "num_var41", "num_var46_0", "num_var46", "saldo_var28", "saldo_var27", "saldo_var41", "saldo_var46", "imp_amort_var18_hace3", "imp_amort_var34_hace3", "imp_reemb_var13_hace3", "imp_reemb_var33_hace3", "imp_trasp_var17_out_hace3", "imp_trasp_var33_out_hace3", "num_var2_0_ult1", "num_var2_ult1", "num_reemb_var13_hace3", "num_reemb_var33_hace3", "num_trasp_var17_out_hace3", "num_trasp_var33_out_hace3", "saldo_var2_ult1", "saldo_medio_var13_medio_hace3");

}
