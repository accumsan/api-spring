package com.minhdd.app.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
@PropertySource("classpath:/config/dev.properties")
public class DevConfiguration {
	
    @Bean
    public JedisConnectionFactory jedisConnFactory() {
		JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
        return jedisConnFactory;
    }
}
