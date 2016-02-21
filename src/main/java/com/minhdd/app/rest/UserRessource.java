package com.minhdd.app.rest;

import com.minhdd.app.repository.StringRedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by minhdao on 20/02/16.
 */
@RestController
@RequestMapping("/api/user")
public class UserRessource {
    private final Logger logger = LoggerFactory.getLogger(UserRessource.class);

    @Inject
    private StringRedisRepository userRepository;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity get(){
        Map<String, String> users = userRepository.findAllToMap();
        return new ResponseEntity(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity get(@PathVariable String key){
        String user = userRepository.findByKey(key);
        Map<String, String> response = new HashMap();
        response.put("user", user);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity post(@RequestBody String payload){
        userRepository.save(payload);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity post(@PathVariable String key, @RequestBody String payload){
        userRepository.save(key, payload);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity delete(@PathVariable String key){
        userRepository.delete(key);
        return new ResponseEntity(HttpStatus.OK);
    }

}
