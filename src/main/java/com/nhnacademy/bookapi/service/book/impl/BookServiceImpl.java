package com.nhnacademy.bookapi.service.book.impl;

import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDetail;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.exception.BookCreatorNotFoundException;
import com.nhnacademy.bookapi.exception.BookIndexNotFoundException;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import com.nhnacademy.bookapi.repository.BookIndexRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.service.book.BookService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookCreatorRepository bookCreatorRepository;
    private final BookIndexRepository bookIndexRepository;

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book update(Book book) {
        Book selectBook = bookRepository.findById(book.getId()).orElse(null);
        if(selectBook == null) {
            throw new BookNotFoundException("book not found");
        }
        BeanUtils.copyProperties(book, selectBook, "id");

        return selectBook;
    }

    @Override
    public void delete(Long id) {
        boolean exist = bookRepository.existsById(id);
        if(!exist) {
            throw new BookNotFoundException("book not found");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public SearchBookDetail searchBookDetailByBookId(Long id) {

        SearchBookDetail searchBookDetail = bookRepository.searchBookById(id).orElse(null);
        if(searchBookDetail == null) {
            throw new BookNotFoundException("Book not found");
        }

        List<BookCreatorDetail> creatorDetails = bookCreatorRepository.findCreatorByBookId(id).stream()
            .map(bookCreator -> {
                BookCreatorDetail bookCreatorDetail = new BookCreatorDetail();
                bookCreatorDetail.setName(bookCreator.getName());
                bookCreatorDetail.setRole(bookCreator.getRole().getDescription());
                return bookCreatorDetail;
            })
            .collect(Collectors.toList());

        // bookCreator -> bookCreatorDetails 변환 role을 한글로 변환

        if(creatorDetails.isEmpty()) {
            throw new BookCreatorNotFoundException("BookCreator not found");
        }
        searchBookDetail.setBookCreators(creatorDetails);

//        List<BookIndex> byBookId = bookIndexRepository.findByBookId(id);

//        searchBookDetail.setBookIndices(byBookId);

        return searchBookDetail;
    }
}
