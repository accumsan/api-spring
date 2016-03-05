package com.minhdd.app.partials.python;

import com.minhdd.app.config.AppProperties;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by minhdao on 02/03/16.
 */
@Component
public class WsToPythonHandler extends TextWebSocketHandler {

    final Logger logger = LoggerFactory.getLogger(WsToPythonHandler.class);
    private List<WebSocketSession> sessions = newArrayList();
    private WebSocket websocket;

    @PostConstruct
    public void init() {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        logger.error("Error occured at session " + session, throwable);
        sessions.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info(String.format("Session %s closed because of %s", session.getId(), status.getReason()));
        sessions.remove(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Connected ... " + session.getId());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info(String.format("Message received from session %s : %s", session.getId(), message.getPayload()));
    }

    public void sendMessage(String message) {
        for (WebSocketSession session : sessions) {
            try {
                logger.info(String.format("Send message {%s}", message));
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                logger.info(String.format("Sending message on error -> close session %s", session.getId()));
                sessions.remove(session);
            }
        }
    }
}
