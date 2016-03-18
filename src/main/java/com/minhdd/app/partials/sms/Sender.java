package com.minhdd.app.partials.sms;

import esendex.sdk.java.EsendexException;
import esendex.sdk.java.ServiceFactory;
import esendex.sdk.java.http.HttpException;
import esendex.sdk.java.model.domain.request.SmsMessageRequest;
import esendex.sdk.java.model.domain.response.MessageResultResponse;
import esendex.sdk.java.model.domain.response.ResourceLinkResponse;
import esendex.sdk.java.service.BasicServiceFactory;
import esendex.sdk.java.service.MessagingService;
import esendex.sdk.java.service.auth.UserPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by mdao on 18/03/2016.
 */
public class Sender {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    //le compte de qs permet de configurer sender (setFrom) mais le compte d'essaie ne le permet pas ??
    //http://developers.esendex.com/Quickstarts/Send-SMS
    //il faut bien mettre sender sinon ca marche pas, il faut pas mettre des caractères bizzare
    public static SmsResultBean sendUsingEsendex(String msg, String sender, String t) {
        SmsResultBean resultBean = new SmsResultBean(SmsResultBean.SendStatus.LOCAL_ERROR);
        String message = msg;
        String target = t;
        if (target == null || message == null) {
            logger.error("La target et/ou le message sont nuls !");
            return resultBean;
        }
        if (!target.startsWith("00") && target.charAt(0) != '+') {
            target = String.format("0033%s", target.substring(1));
        }
        try {
            message = message.replace("ç", "c").replace("é", "e").replace("è", "e").replace("à", "a").replace("ù", "u");
        } catch (Exception e) {
            logger.error("Encoding non supporté !");
            return resultBean;
        }
        try {
            SmsMessageRequest m = new SmsMessageRequest(target, message);
            m.setValidity(1); // renvoi pendant 1h max si erreur
            if (sender != null) {
                m.setFrom(sender);
            }
            BasicServiceFactory factory = ServiceFactory.createBasicAuthenticatingFactory(new UserPassword("md.insa@gmail.com", "krYrZxQcMWRT"));
            MessagingService service = factory.getMessagingService();
            long startTime = System.currentTimeMillis();
            MessageResultResponse resp;
            try {
                resp = service.sendMessage("EX0207664", m);
            } catch (HttpException e) {
                logger.error("Erreur de connexion avec le service d'envoi de SMS Esendex (identifiants invalides ?)", e);
                resultBean.setSendStatus(SmsResultBean.SendStatus.ERROR);
                return resultBean;
            } catch (EsendexException e) { // Indique un problème avec le service
                logger.error("Une erreur est survenue avec le service d'envoi de SMS Esendex", e);
                resultBean.setSendStatus(SmsResultBean.SendStatus.ERROR);
                return resultBean;
            }
            logger.info("Durée d'envoi du SMS Esendex : " + (System.currentTimeMillis() - startTime) + " ms");
            resultBean.setSendStatus(SmsResultBean.SendStatus.ERROR);

            if (resp == null) {
                logger.error("Pas de résultat d'envoi du SMS Esendex");
            } else if (resp.getBatchId() != null && resp.getMessageIds() != null && resp.getMessageIds().size() > 0) {

                // Appel de getMessages sur la réponse pour récupérer la liste d'ID pour le message envoyé
                List<ResourceLinkResponse> messages = resp.getMessageIds();
                ResourceLinkResponse smsResponse = messages.get(0);

                // Passage en statut "envoyé" si OK (on ne sait pas si le SMS est reçu pour l'instant)
                resultBean.setSendStatus(SmsResultBean.SendStatus.SENT);
                resultBean.setBatchId(resp.getBatchId());
                resultBean.setSmsId(smsResponse.getId());

                logger.info("Réponse OK lors de l'envoi du SMS Esendex "
                        + ": batchId=" + resp.getBatchId()
                        + ", smsId=" + smsResponse.getId()
                        + ", uri=" + smsResponse.getUri());
            } else {
                logger.error("Erreur lors de l'envoi du SMS Esendex.");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi du SMS Esendex", e);
        }
        return resultBean;
    }
}