//package com.nhnacademy.bookapi.service.book_index;
//
//import com.nhnacademy.bookapi.dto.book_index.BookIndexRequestDto;
//import com.nhnacademy.bookapi.dto.book_index.BookIndexResponseDto;
//import com.nhnacademy.bookapi.entity.Book;
//import com.nhnacademy.bookapi.entity.BookIndex;
//import com.nhnacademy.bookapi.exception.BookIndexNotFoundException;
//import com.nhnacademy.bookapi.exception.BookNotFoundException;
//import com.nhnacademy.bookapi.repository.BookIndexRepository;
//import com.nhnacademy.bookapi.repository.BookRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//
//@Service
//@Transactional
//public class BookIndexService {
//
//    private BookIndexRepository bookIndexRepository;
//    private BookRepository bookRepository;
//
//    public BookIndexService(BookIndexRepository bookIndexRepository, BookRepository bookRepository) {
//        this.bookIndexRepository = bookIndexRepository;
//        this.bookRepository = bookRepository;
//    }
//
//    public boolean addIndex(BookIndexRequestDto bookIndexRequestDto) { // 특정 책에 목차 생성
//
//        Book book = bookRepository.findById(bookIndexRequestDto.getBookId()).orElseThrow(() -> new BookNotFoundException("Book not found"));
//
//        if (bookIndexRepository.existsByBookId(bookIndexRequestDto.getBookId())) {
//            throw new BookIndexNotFoundException("Already exist");
//        }
//        bookIndexRepository.save(new BookIndex(bookIndexRequestDto.getBookText(), book));
//        return true;
//    }
//
//    public List<BookIndexResponseDto> getIndicesByBook(Long bookId) { // 특정 책의 모든 목차 조회
//        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book not found"));
//        Optional<List<BookIndex>> bookIndex = bookIndexRepository.findByBook(book);
//        if (bookIndex.isEmpty()) {
//            throw new BookNotFoundException("BookIndex not found");
//        }
//        List<BookIndexResponseDto> result = new ArrayList<>();
//        for (BookIndex bookIndexItem : bookIndex.get()) {
//            result.add(new BookIndexResponseDto(bookIndexItem.getIndexText()));
//        }
//        return result;
//    }
//
//    //수정하려는 book_id를 이용해 어떤 목차를 수정할 것인지
//    public boolean updateIndex(BookIndexRequestDto bookIndexRequestDto) { // bookIndexRequestDto에는 수정하려는 bookId와 sequence를 통해 bookIndex 수정
//        Book book = bookRepository.findById(bookIndexRequestDto.getBookId()).orElseThrow(() -> new BookNotFoundException("Book not found"));
//        Optional<BookIndex> bookIndex = bookIndexRepository.findByBookAndSequence(book);
//        if (bookIndex.isEmpty()) {
//            throw new BookIndexNotFoundException("BookIndex not found");
//        }
//        bookIndex.get().setTitle(bookIndexRequestDto.getTitle());
//        bookIndex.get().setNumber(bookIndexRequestDto.getNumber());
//        return true;
//    }
//
//    public boolean deleteIndex(Long indexId) { // 삭제 하려는 indexId를 파라미터로 받아서 해당 컬럼 삭제
//        if (!bookIndexRepository.existsById(indexId)) {
//            throw new BookIndexNotFoundException("BookIndex not found");
//        }
//        bookIndexRepository.deleteById(indexId);
//        return true;
//    }
//
//
//
//}

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
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));

        BookIndex bookIndex = bookIndexRepository.findByBook(book)
                .orElseThrow(() -> new BookIndexNotFoundException("Book index not found for this book."));

        // 삭제
        bookIndexRepository.deleteById(bookIndex.getId());
        return true;
    }
}


