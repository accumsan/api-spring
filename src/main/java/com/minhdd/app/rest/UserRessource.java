package com.minhdd.app.rest;

import com.minhdd.app.repository.StringRedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by minhdao on 20/02/16.
 */
@RestController
@RequestMapping("/api/users")
public class UserRessource {
    private final Logger logger = LoggerFactory.getLogger(UserRessource.class);

    @Inject
    private StringRedisRepository userRepository;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity get(){
        Map<String, String> response = new HashMap<String, String>();
        response.put("message", "spring rest");
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity post(@PathVariable String id, @RequestBody String payload){
        userRepository.save(id, payload);
        return new ResponseEntity(HttpStatus.OK);
    }
}
