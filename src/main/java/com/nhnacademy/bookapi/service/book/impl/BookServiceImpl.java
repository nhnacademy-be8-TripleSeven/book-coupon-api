package com.nhnacademy.bookapi.service.book.impl;

import com.nhnacademy.bookapi.dto.book.SearchBookDetailDTO;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.exception.BookCreatorNotFoundException;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.service.book.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookCreatorRepository bookCreatorRepository;

    @Override
    public SearchBookDetailDTO searchBookDetailByBookId(Long id) {

        SearchBookDetailDTO searchBookDetailDTO = bookRepository.searchBookById(id).orElse(null);
        if(searchBookDetailDTO == null) {
            throw new BookNotFoundException("Book not found");
        }
        List<BookCreator> creatorByBookId = bookCreatorRepository.findCreatorByBookId(id);
        if(creatorByBookId.isEmpty()) {
            throw new BookCreatorNotFoundException("BookCreator not found");
        }
        searchBookDetailDTO.setBookCreators(creatorByBookId);

        return searchBookDetailDTO;
    }
}
