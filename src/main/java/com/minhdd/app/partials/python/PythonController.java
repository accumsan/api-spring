package com.minhdd.app.partials.python;

import com.neovisionaries.ws.client.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Created by minhdao on 02/03/16.
 */
@RestController
@RequestMapping("/api/python")
public class PythonController {
    private final Logger logger = LoggerFactory.getLogger(PythonController.class);
    @Inject
    private WebSocket outWsPython;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void post(@RequestBody String message) {
        outWsPython.sendText(message);
    }

}
