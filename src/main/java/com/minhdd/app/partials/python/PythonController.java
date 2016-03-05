package com.minhdd.app.partials.python;

import com.minhdd.app.config.AppProperties;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by minhdao on 02/03/16.
 */
@RestController
@RequestMapping("/api/python")
public class PythonController {
    private final Logger logger = LoggerFactory.getLogger(PythonController.class);
    @Inject
    private WebSocket outWsPython;

    @Inject
    WsToPythonHandler wsToPythonHandler;

    @RequestMapping(value="/connect/out", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void out() {
        try {
            outWsPython.connect();
        } catch (WebSocketException e) {
            logger.error("Error connecting outbox for python server with websockets");
        }
    }

    @RequestMapping(value="/connect/in", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void in() {
        try {
            WebSocket websocket = new WebSocketFactory()
                    .createSocket("ws://" + AppProperties.getInstance().getProperty("python.server") + "/tojava")
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket ws, String message) {
                            logger.info("Received msg from python: " + message);
                            wsToPythonHandler.sendMessage(message);
                        }
                    }).connect();
        } catch (IOException e) {
            logger.error("Error initializing websockets inbox for python server");
        } catch (WebSocketException e) {
            logger.error("Error connecting websockets inbox for python server");
        }
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void post(@RequestBody String message) {
        outWsPython.sendText(message);
    }

}
