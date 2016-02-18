package com.minhdd.app.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.minhdd.app.domain.Book;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Redis repository for the Book entity.
 */
@Repository
public class BookRepository {
	
	@Inject
	private RedisTemplate<String, Book> bookTemplate;

	final private String BOOK_KEY_PREFIX = "book-";
	final private String BOOK_KEY_REGREX = "book-*";
	
	public void save(Book book) {
		bookTemplate.opsForValue().set(BOOK_KEY_PREFIX+book.getId(), book);
	}
 
	public Book findById(String id) {
		return bookTemplate.opsForValue().get(BOOK_KEY_PREFIX+id);
	}

	private Book findByKey(String key) {
		return bookTemplate.opsForValue().get(key);
	}
	
	public List<Book> findAll() {
		List<Book> books = new ArrayList<>();
		
		Set<String> keys = bookTemplate.keys(BOOK_KEY_REGREX);
		Iterator<String> it = keys.iterator();
		
		while(it.hasNext()){
			books.add(findByKey(it.next()));
		}
		
		return books;
	}



	public void delete(Book b) {
		String key = b.getId();
		bookTemplate.opsForValue().getOperations().delete(BOOK_KEY_PREFIX+key);
	}
	
	 
	public void deleteAll() {
		Set<String> keys = bookTemplate.keys(BOOK_KEY_REGREX);
		Iterator<String> it = keys.iterator();
		
		while(it.hasNext()){
			bookTemplate.opsForValue().getOperations().delete(it.next());
		}
	}
}
