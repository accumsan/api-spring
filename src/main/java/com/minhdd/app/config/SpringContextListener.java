package com.minhdd.app.config;

import javax.inject.Inject;

import com.minhdd.app.repository.StringRedisRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.minhdd.app.service.BookService;

@Component
public class SpringContextListener implements ApplicationListener<ContextRefreshedEvent> {
	
	@Inject 
	private BookService bookService;

	@Inject
	private StringRedisRepository recordRepository;

	@Inject
	private StringRedisRepository userRepository;

	/**
	 * This method will run on application startup and load up the default book
	 * collection into the Redis database.
	 */
	public void onApplicationEvent(ContextRefreshedEvent event) {
		bookService.restoreDefaultBooks();
		recordRepository.init();
		userRepository.init();
	};
}
