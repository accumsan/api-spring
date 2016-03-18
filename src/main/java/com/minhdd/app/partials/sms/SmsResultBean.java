package com.minhdd.app.partials.sms;

import java.io.Serializable;

/**
 * Bean permettant de stocker un statut de retour sur envoi d'un SMS de manière
 * uniforme, quelque soit le provider SMS.
 *
 * @version 1.0
 */
public class SmsResultBean implements Serializable {

    public static enum SendStatus {
        LOCAL_ERROR,
        ERROR,
        SENT,
        RECEIVED
    }

    private static final long serialVersionUID = -6377888716096137031L;

    // ====== Variables standard ============================================ //

    private Long transactionId;

    /**
     * Utilis� pour d�terminer si le SMS a �t� re�u par son destinataire ou pas
     */
    private SendStatus sendStatus = null;

    /**
     * L'ID du groupe de SMS envoy�s tel qu'il a �t� retourn� par la plateforme d'envoi
     */
    private String batchId = null;

    /**
     * L'ID du SMS tel qu'il a �t� retourn� par la plateforme d'envoi
     */
    private String smsId = null;

    // ====== Constructors ================================================== //

    public SmsResultBean() {

    }

    public SmsResultBean(SendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    // ====== Getters et Setters ============================================ //

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public SendStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(SendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }
}