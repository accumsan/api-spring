package com.minhdd.app.config;

import javax.inject.Inject;

import com.minhdd.app.domain.Book;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class DatabaseConfiguration {

	@Inject
	private JedisConnectionFactory jedisConnFactory;
	
    @Bean
    public StringRedisSerializer stringRedisSerializer() {
    	StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    	return stringRedisSerializer;
    }
    
    @Bean
    public JacksonJsonRedisSerializer<Book> jacksonJsonRedisJsonSerializer() {
    	JacksonJsonRedisSerializer<Book> jacksonJsonRedisJsonSerializer = new JacksonJsonRedisSerializer<>(Book.class);
    	return jacksonJsonRedisJsonSerializer;
    }
    
	@Bean
	public RedisTemplate<String, Book> bookTemplate() {
		RedisTemplate<String, Book> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnFactory);
		redisTemplate.setKeySerializer(stringRedisSerializer());
		redisTemplate.setValueSerializer(jacksonJsonRedisJsonSerializer());
		return redisTemplate;
	}

	@Bean
	public RedisTemplate<String, String> stringTemplate() {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnFactory);
		redisTemplate.setKeySerializer(stringRedisSerializer());
		redisTemplate.setValueSerializer(stringRedisSerializer());
		return redisTemplate;
	}
}
