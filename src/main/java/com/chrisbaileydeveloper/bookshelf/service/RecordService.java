package com.chrisbaileydeveloper.bookshelf.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by minhdao on 18/02/16.
 */
@Service
public class RecordService {
    @Inject
    private RedisTemplate<String, String> redisTemplate;

    private final String RECORD_NEXTVAL = "record_nextval";
    private final String RECORD_KEY_PREFIX = "record-";
    private final String RECORD_KEY_REGREX = RECORD_KEY_PREFIX + "*";

    public void init() {
        if (redisTemplate.keys(RECORD_NEXTVAL).isEmpty()) {
            redisTemplate.opsForValue().set(RECORD_NEXTVAL, "0");
        }
    }

    private String getNextVal() {
        return redisTemplate.opsForValue().get(RECORD_NEXTVAL);
    }

    private String getNextKey(){
        return RECORD_KEY_PREFIX + getNextVal();
    }

    private void setNextKey() {
        int nextVal = Integer.valueOf(getNextVal()) + 1;
        redisTemplate.opsForValue().set(RECORD_NEXTVAL, String.valueOf(nextVal));
    }

    private String findById(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public List<String> findAll() {
        List<String> records = new ArrayList<>();
        Set<String> keys = redisTemplate.keys(RECORD_KEY_REGREX);
        Iterator<String> it = keys.iterator();

        while(it.hasNext()){
            records.add(findById(it.next()));
        }
        return records;
    }
    public void save(String record) {
        redisTemplate.opsForValue().set(getNextKey(), record);
        setNextKey();
    }
}
