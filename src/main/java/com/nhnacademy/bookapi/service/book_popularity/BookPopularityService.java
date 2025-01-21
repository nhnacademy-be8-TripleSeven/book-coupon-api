package com.nhnacademy.bookapi.service.book_popularity;

import com.nhnacademy.bookapi.entity.BookPopularity;
import com.nhnacademy.bookapi.exception.BookPopularityNotFoundException;
import com.nhnacademy.bookapi.repository.BookPopularityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookPopularityService {

    private final BookPopularityRepository bookPopularityRepository;

    @Transactional
    public void updateSearchRank(long bookId, long popularity) {
        BookPopularity bookPopularity = bookPopularityRepository.findByBookId(bookId)
            .orElseThrow(() -> new BookPopularityNotFoundException("BookPopularity not found"));
        bookPopularity.updateSearchRank(popularity);
    }
}