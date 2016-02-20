package com.minhdd.app.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by minhdao on 20/02/16.
 */
@Repository
public abstract class StringRedisRepository {
    @Inject
    private RedisTemplate<String, String> stringTemplate;

    private String NEXTVAL;
    private String KEY_PREFIX;
    private String KEY_REGREX;

    public StringRedisRepository(String nextval, String key_prefix, String key_regrex) {
        NEXTVAL = nextval;
        KEY_PREFIX = key_prefix;
        KEY_REGREX = key_regrex;
    }

    public void init() {
        if (stringTemplate.keys(NEXTVAL).isEmpty()) {
            stringTemplate.opsForValue().set(NEXTVAL, "0");
        }
    }
    private String nextVal() {
        return stringTemplate.opsForValue().get(NEXTVAL);
    }

    private String nextKey(){
        return KEY_PREFIX + nextVal();
    }

    private void setNextKey() {
        int nextVal = Integer.valueOf(nextVal()) + 1;
        stringTemplate.opsForValue().set(NEXTVAL, String.valueOf(nextVal));
    }

    private String findByKey(String key) {
        return stringTemplate.opsForValue().get(key);
    }
    public String findById(String id) {
        return stringTemplate.opsForValue().get(KEY_PREFIX+id);
    }

    public List<String> findAll() {
        List<String> records = new ArrayList<>();
        Set<String> keys = stringTemplate.keys(KEY_REGREX);
        Iterator<String> it = keys.iterator();

        while(it.hasNext()){
            records.add(findByKey(it.next()));
        }
        return records;
    }

    public void save(String record) {
        stringTemplate.opsForValue().set(nextKey(), record);
        setNextKey();
    }

    public void save(String id, String record) {
        stringTemplate.opsForValue().set(KEY_PREFIX + id, record);
    }

    public void delete(String id) {
        stringTemplate.opsForValue().getOperations().delete(KEY_PREFIX + id);
    }
}
