
package com.nhnacademy.bookapi.service.book_index;

import com.nhnacademy.bookapi.dto.book_index.BookIndexRequestDto;
import com.nhnacademy.bookapi.dto.book_index.BookIndexResponseDto;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.exception.BookIndexAlreadyExistException;
import com.nhnacademy.bookapi.exception.BookIndexNotFoundException;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.BookIndexRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BookIndexService {

    private final BookIndexRepository bookIndexRepository;
    private final BookRepository bookRepository;

    /**
     * 특정 책에 목차 생성
     */
    public boolean addIndex(BookIndexRequestDto bookIndexRequestDto) {
        // 책 조회 및 예외 처리
        Book book = bookRepository.findById(bookIndexRequestDto.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookIndexRequestDto.getBookId()));

        // 동일한 책에 목차가 이미 존재하면 예외 발생
        if (bookIndexRepository.existsByBook(book)) {
            throw new BookIndexAlreadyExistException("Book index already exist");
        }

        // 목차 저장
        BookIndex bookIndex = new BookIndex(bookIndexRequestDto.getIndexText(), book);
        bookIndexRepository.save(bookIndex);
        return true;
    }


    public boolean updateIndex(BookIndexRequestDto bookIndexRequestDto) {
        // 책 조회 및 예외 처리
        Book book = bookRepository.findById(bookIndexRequestDto.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookIndexRequestDto.getBookId()));

        // 책과 연결된 목차 조회
        BookIndex bookIndex = bookIndexRepository.findByBook(book)
                .orElseThrow(() -> new BookIndexNotFoundException("Book index not found for this book."));

        // 목차 텍스트 수정
        bookIndex.updateIndexText(bookIndexRequestDto.getIndexText());
        return true;
    }

    /**
     * 특정 목차 삭제
     */
    public boolean deleteIndex(Long bookId) {
        // 삭제 대상 목차 존재 여부 확인 및 예외 처리

        Book book = bookRepository.findById(bookId).orElse(null);


        BookIndex bookIndex = bookIndexRepository.findByBook(book).orElse(null);


        if(bookIndex != null) {

            bookIndexRepository.deleteById(bookIndex.getId());
        }

        return true;
    }

    public String getBookIndexList(long bookId) {
        return bookIndexRepository.findByBookId(bookId);
    }

    public BookIndex getBookIndex(long bookId) {
        return bookIndexRepository.findById(bookId).orElse(null);
    }

    public void createBookIndex(BookIndex bookIndex){
        bookIndexRepository.save(bookIndex);
    }

    public void deleteBookIndex(long bookId) {
        bookIndexRepository.deleteById(bookId);

    }
}


