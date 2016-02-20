package com.minhdd.app.config;

import com.minhdd.app.repository.StringRedisRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by minhdao on 20/02/16.
 */

@Configuration
public class RepositoryConfiguration {

    @Bean
    public StringRedisRepository userRepository() {
        return new StringRedisRepository("user_nextval", "user-", "user-*"){};
    }

    @Bean
    public StringRedisRepository recordRepository() {
        return new StringRedisRepository("record_nextval", "record-", "record-*"){};
    }

}
