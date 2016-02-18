package com.minhdd.app.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by minhdao on 18/02/16.
 */
@RestController
@RequestMapping("/rest/test")
public class TestRessource {
    private final Logger logger = LoggerFactory.getLogger(TestRessource.class);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> get(@PathVariable int id){
        Map<String, String> response = new HashMap<String, String>();
        response.put("id", String.valueOf(id));
        response.put("message", "spring rest");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
