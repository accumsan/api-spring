package com.minhdd.app.ml.service.kaggle.scs;

import org.apache.commons.lang3.ArrayUtils;

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
    public final static String PCA_10 = OUTPUT_DIR + "pca10.model";
    public final static String PCA_50 = OUTPUT_DIR + "pca50.model";
    public final static String PCA = OUTPUT_DIR + "pca.model"; //pca 260
    public final static String PCA_261 = OUTPUT_DIR + "pca261.model";
    public final static String PCA_308 = OUTPUT_DIR + "pca308.model";
    public final static String PCA_200 = OUTPUT_DIR + "pca200.model";
    public final static String PCA_290 = OUTPUT_DIR + "pca290.model";
    public final static String CHISQ = OUTPUT_DIR + "chisq.model";
    public final static String RFP_MODEL = OUTPUT_DIR + "random-forest-pipeline.model";
    public final static String GBT_MODEL = OUTPUT_DIR + "gradient-boosted-pipeline.model";
    public final static String LR_MODEL = OUTPUT_DIR + "logistic-regression.model";
    public final static String BinaryClassification_MODEL = OUTPUT_DIR + "binary-classification.model";
    public final static String TEST_OUTPUT = OUTPUT_DIR + "predictions.csv";

    public final static String LOCAL_DIR_QS = "/Users/mdao/ws/minh/ml/kaggle/santander-customer-satisfaction/";
    public final static String LOCAL_DIR_MAC86 = "/Users/minhdao/Workspace/ml/kaggle/santander-customer-satisfaction/data/";
    /****************************** to modify ***************************/
    public final static String LOCAL_DIR = LOCAL_DIR_MAC86;
    /*******************************************************************/
    public final static String LOCAL_DIR_EXTRACT = LOCAL_DIR + "extracts/";
    public final static String LOCAL_DIR_ANO_DETECT = LOCAL_DIR + "ano-detect/";
    public final static String TRAIN_ORIGINAL_KAGGLE = LOCAL_DIR + "train.csv";
    public final static String TRAIN_DEDUPLICATED_KAGGLE = LOCAL_DIR + "train_deduplicated.csv";
    public final static String TEST_KAGGLE = LOCAL_DIR + "test.csv";
    public static final String TRAIN_80 = LOCAL_DIR_EXTRACT + "train_80.csv";
    public static final String TRAIN_60 = LOCAL_DIR_EXTRACT + "train_60.csv";
    public static final String TRAIN_50 = LOCAL_DIR_EXTRACT + "train_50.csv";
    public static final String VALIDATION_40 = LOCAL_DIR_EXTRACT + "validation_40.csv";
    public static final String VALIDATION_20 = LOCAL_DIR_EXTRACT + "validation_20.csv";
    public static final String TEST_20 = LOCAL_DIR_EXTRACT + "test_20.csv";
    public static final String TEST_10 = LOCAL_DIR_EXTRACT + "test_10.csv";
    public static final String TRAIN_ANO_DETECT = LOCAL_DIR_ANO_DETECT + "ano_detect_train.csv";
    public static final String VALIDATION_ANO_DETECT = LOCAL_DIR_ANO_DETECT + "ano_detect_validation.csv";
    public static final String TEST_ANO_DETECT = LOCAL_DIR_ANO_DETECT + "ano_detect_test.csv";

    public static final String[] ZEROS_COLUMNS = new String[]{"ind_var2_0", "ind_var2", "ind_var27_0", "ind_var28_0", "ind_var28", "ind_var27", "ind_var41", "ind_var46_0", "ind_var46", "num_var27_0", "num_var28_0", "num_var28", "num_var27", "num_var41", "num_var46_0", "num_var46", "saldo_var28", "saldo_var27", "saldo_var41", "saldo_var46", "imp_amort_var18_hace3", "imp_amort_var34_hace3", "imp_reemb_var13_hace3", "imp_reemb_var33_hace3", "imp_trasp_var17_out_hace3", "imp_trasp_var33_out_hace3", "num_var2_0_ult1", "num_var2_ult1", "num_reemb_var13_hace3", "num_reemb_var33_hace3", "num_trasp_var17_out_hace3", "num_trasp_var33_out_hace3", "saldo_var2_ult1", "saldo_medio_var13_medio_hace3"};
    public static final String[] REDUNDANT_COLUMNS = new String[]{"num_var44", "ind_var20_0", "num_var8", "num_var7_emit_ult1", "num_var20", "delta_num_reemb_var13_1y3", "num_reemb_var13_ult1", "saldo_medio_var13_medio_ult1" , "saldo_var29", "delta_num_trasp_var33_in_1y3", "delta_num_reemb_var17_1y3", "num_var32_0", "ind_var32_0", "num_var25_0", "num_var26_0", "num_var37_0", "ind_var25_0", "ind_var26_0", "ind_var37_0", "ind_var29_0", "num_var6_0", "num_var29_0", "ind_var39", "num_var40", "num_var39", "num_var34_0", "num_var18_0", "num_var13_medio_0", "ind_var34_0", "ind_var18_0", "ind_var13_medio_0"};
    public static final String[] EXCLUDED_COLUMNS = (String[]) ArrayUtils.addAll(ZEROS_COLUMNS, REDUNDANT_COLUMNS);
    public static final String[] more_than_10000_distinct_values = new String[]{"var38", "saldo_medio_var5_ult3", "saldo_var30"};
//    public static final String[] EXCLUDED_COLUMNS = (String[]) ArrayUtils.addAll(EXCLUDED_COLUMNS_1, more_than_10000_distinct_values);
    public static final String[] SELECTED_COLUMNS = new String[]{"var3", "var15", "imp_op_var39_comer_ult1", "imp_op_var39_comer_ult3", "imp_op_var40_comer_ult1", "imp_op_var40_efect_ult1", "imp_op_var40_efect_ult3", "imp_op_var40_ult1", "imp_op_var41_comer_ult1", "imp_op_var41_comer_ult3", "imp_op_var41_efect_ult1", "imp_op_var41_efect_ult3", "imp_op_var41_ult1", "imp_op_var39_efect_ult1", "imp_op_var39_efect_ult3", "imp_op_var39_ult1", "ind_var1_0", "ind_var1", "ind_var5_0", "ind_var5", "ind_var6_0", "ind_var6", "ind_var8_0", "ind_var8", "ind_var12_0", "ind_var12", "ind_var13_0", "ind_var13_corto_0", "ind_var13_corto", "ind_var13_largo_0", "ind_var13_largo", "ind_var13_medio_0", "ind_var13_medio", "ind_var13", "ind_var14_0", "ind_var14", "ind_var17_0", "ind_var17", "ind_var18_0", "ind_var18", "ind_var19", "ind_var20_0", "ind_var20", "ind_var24_0", "ind_var24", "ind_var25_cte", "ind_var26_0", "ind_var26_cte", "ind_var26", "ind_var25_0", "ind_var25", "ind_var29_0", "ind_var29", "ind_var30", "ind_var31_0", "ind_var31", "ind_var32_cte", "ind_var33_0", "ind_var33", "ind_var34_0", "ind_var34", "ind_var37_cte", "ind_var37_0", "ind_var37", "ind_var39_0", "ind_var40_0", "ind_var40", "ind_var41_0", "ind_var39", "ind_var44_0", "ind_var44", "num_var1_0", "num_var1", "num_var4", "num_var5_0", "num_var5", "num_var6_0", "num_var6", "num_var8_0", "num_var8", "num_var12_0", "num_var12", "num_var13_0", "num_var13_corto_0", "num_var13_corto", "num_var13_largo_0", "num_var13_largo", "num_var13_medio_0", "num_var13_medio", "num_var13", "num_var14_0", "num_var14", "num_var17_0", "num_var17", "num_var18_0", "num_var18", "num_var20_0", "num_var20", "num_var24_0", "num_var24", "num_var26_0", "num_var26", "num_var25_0", "num_var25", "num_op_var40_hace2", "num_op_var40_hace3", "num_op_var40_ult1", "num_op_var41_hace2", "num_op_var41_hace3", "num_op_var41_ult1", "num_op_var41_ult3", "num_op_var39_hace2", "num_op_var39_hace3", "num_op_var39_ult1", "num_op_var39_ult3", "num_var29_0", "num_var29", "num_var30_0", "num_var30", "num_var31_0", "num_var31", "num_var33_0", "num_var33", "num_var34_0", "num_var34", "num_var35", "num_var37_0", "num_var37", "num_var39_0", "num_var40_0", "num_var40", "num_var41_0", "num_var39", "num_var42_0", "num_var42", "num_var44_0", "num_var44", "saldo_var5", "saldo_var6", "saldo_var8", "saldo_var12", "saldo_var13_corto", "saldo_var13_largo", "saldo_var13_medio", "saldo_var13", "saldo_var14", "saldo_var17", "saldo_var18", "saldo_var20", "saldo_var24", "saldo_var26", "saldo_var25", "saldo_var29", "saldo_var30", "saldo_var31", "saldo_var33", "saldo_var34", "saldo_var37", "saldo_var40", "saldo_var42", "saldo_var44", "var36", "delta_imp_amort_var18_1y3", "delta_imp_amort_var34_1y3", "delta_imp_aport_var13_1y3", "delta_imp_aport_var17_1y3", "delta_imp_compra_var44_1y3", "delta_imp_reemb_var13_1y3", "delta_imp_reemb_var17_1y3", "delta_imp_reemb_var33_1y3", "delta_imp_trasp_var17_in_1y3", "delta_imp_trasp_var17_out_1y3", "delta_imp_trasp_var33_in_1y3", "delta_imp_venta_var44_1y3", "delta_num_aport_var13_1y3", "delta_num_aport_var17_1y3", "delta_num_compra_var44_1y3", "delta_num_reemb_var13_1y3", "delta_num_reemb_var17_1y3", "delta_num_trasp_var17_in_1y3", "delta_num_trasp_var17_out_1y3", "delta_num_trasp_var33_in_1y3", "delta_num_venta_var44_1y3", "imp_amort_var18_ult1", "imp_amort_var34_ult1", "imp_aport_var13_hace3", "imp_aport_var13_ult1", "imp_aport_var17_hace3", "imp_aport_var17_ult1", "imp_aport_var33_hace3", "imp_aport_var33_ult1", "imp_var7_emit_ult1", "imp_var7_recib_ult1", "imp_compra_var44_hace3", "imp_compra_var44_ult1", "imp_reemb_var13_ult1", "imp_reemb_var17_ult1", "imp_var43_emit_ult1", "imp_trans_var37_ult1", "imp_trasp_var17_in_hace3", "imp_trasp_var17_in_ult1", "imp_trasp_var17_out_ult1", "imp_trasp_var33_in_hace3", "imp_trasp_var33_in_ult1", "imp_venta_var44_hace3", "imp_venta_var44_ult1", "ind_var7_emit_ult1", "ind_var7_recib_ult1", "ind_var10_ult1", "ind_var10cte_ult1", "ind_var9_cte_ult1", "ind_var9_ult1", "ind_var43_emit_ult1", "ind_var43_recib_ult1", "var21", "num_aport_var13_hace3", "num_aport_var13_ult1", "num_aport_var17_hace3", "num_aport_var17_ult1", "num_aport_var33_hace3", "num_aport_var33_ult1", "num_var7_emit_ult1", "num_compra_var44_hace3", "num_compra_var44_ult1", "num_ent_var16_ult1", "num_var22_hace2", "num_var22_hace3", "num_var22_ult1", "num_var22_ult3", "num_med_var22_ult3", "num_med_var45_ult3", "num_meses_var5_ult3", "num_meses_var8_ult3", "num_meses_var12_ult3", "num_meses_var13_corto_ult3", "num_meses_var13_largo_ult3", "num_meses_var13_medio_ult3", "num_meses_var17_ult3", "num_meses_var29_ult3", "num_meses_var33_ult3", "num_meses_var39_vig_ult3", "num_meses_var44_ult3", "num_op_var39_comer_ult1", "num_op_var40_comer_ult1", "num_op_var40_comer_ult3", "num_op_var40_efect_ult1", "num_op_var40_efect_ult3", "num_op_var41_comer_ult1", "num_op_var41_comer_ult3", "num_op_var41_efect_ult1", "num_op_var41_efect_ult3", "num_op_var39_efect_ult1", "num_op_var39_efect_ult3", "num_reemb_var13_ult1", "num_reemb_var17_ult1", "num_sal_var16_ult1", "num_var43_emit_ult1", "num_var43_recib_ult1", "num_trasp_var11_ult1", "num_trasp_var17_in_hace3", "num_trasp_var17_in_ult1", "num_trasp_var17_out_ult1", "num_trasp_var33_in_hace3", "num_trasp_var33_in_ult1", "num_venta_var44_hace3", "num_venta_var44_ult1", "num_var45_hace2", "num_var45_hace3", "num_var45_ult1", "num_var45_ult3", "saldo_medio_var5_hace2", "saldo_medio_var5_hace3", "saldo_medio_var5_ult1", "saldo_medio_var5_ult3", "saldo_medio_var8_hace2", "saldo_medio_var8_hace3", "saldo_medio_var8_ult1", "saldo_medio_var8_ult3", "saldo_medio_var12_hace2", "saldo_medio_var12_hace3", "saldo_medio_var12_ult1", "saldo_medio_var12_ult3", "saldo_medio_var13_corto_hace2", "saldo_medio_var13_corto_hace3", "saldo_medio_var13_corto_ult1", "saldo_medio_var13_corto_ult3", "saldo_medio_var13_largo_hace2", "saldo_medio_var13_largo_hace3", "saldo_medio_var13_largo_ult1", "saldo_medio_var13_largo_ult3", "saldo_medio_var13_medio_hace2", "saldo_medio_var13_medio_ult1", "saldo_medio_var13_medio_ult3", "saldo_medio_var17_hace2", "saldo_medio_var17_hace3", "saldo_medio_var29_hace2", "saldo_medio_var29_ult1", "saldo_medio_var29_ult3", "saldo_medio_var33_hace2", "saldo_medio_var33_hace3", "saldo_medio_var33_ult1", "saldo_medio_var33_ult3", "saldo_medio_var44_hace2", "saldo_medio_var44_hace3", "saldo_medio_var44_ult1", "saldo_medio_var44_ult3", "var38", "n0"};


}
