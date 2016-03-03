package com.minhdd.app.partials.python;

import com.minhdd.app.config.AppProperties;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by minhdao on 02/03/16.
 */
@Configuration
@EnableWebSocket
public class WebsocketConfiguration implements WebSocketConfigurer {
    final Logger logger = LoggerFactory.getLogger(WebsocketConfiguration.class);

    @Inject
    protected WsToPythonHandler wsToPythonHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wsToPythonHandler, "/python").setAllowedOrigins("*");
    }

    @Bean
    public WebSocket outWsPython() {
        WebSocket websocket = null;
        try {
            websocket = new WebSocketFactory().createSocket("ws://" + AppProperties.getInstance().getProperty("python.server") + "/java");
        } catch (IOException e) {
            logger.error("Error initializing websockets outbox for python server");
        } finally {
            return websocket;
        }

    }
}
