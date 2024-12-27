package com.nhnacademy.bookapi.service.book_popularity;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookPopularity;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.BookPopularityRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookPopularityService {

    private final BookPopularityRepository bookPopularityRepository;
    private final BookRepository bookRepository;

    public BookPopularityService(BookPopularityRepository bookPopularRepository,
                                 BookRepository bookRepository) {
        this.bookPopularityRepository = bookPopularRepository;
        this.bookRepository = bookRepository;
    }

    public void countUpClick(Long bookId) {
        if (!bookPopularityRepository.existsById(bookId)) {
            Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book not found"));
            bookPopularityRepository.save(new BookPopularity(book, 0, 0, 0));
        } else {
            BookPopularity bookPopularity = bookPopularityRepository.findById(bookId).get();
            bookPopularity.update(bookPopularity.getClickRank() + 1, bookPopularity.getSearchRank(), bookPopularity.getCartCount());
        }
    }
}
