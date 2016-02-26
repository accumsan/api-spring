package com.minhdd.app.partials.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.thymeleaf.util.StringUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by minhdao on 26/02/16.
 */
@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    final Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        logger.error("Error occured at session " + session, throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info(String.format("Session %s closed because of %s", session.getId(), status.getReason()));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Connected ... " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info(String.format("Message received from session %s : %s", session.getId(), message.getPayload()));
        String mes = message.getPayload();
        if (mes.equals("random")) {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
            RandomSender randomSender = new RandomSender(50, session);
            for (int i=1; i<6; i++) executorService.schedule(randomSender, 1*i, TimeUnit.SECONDS);
        } else {
            session.sendMessage(new TextMessage(mes));
        }
    }
}