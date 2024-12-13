package com.nhnacademy.bookapi.service.book_index;

import com.nhnacademy.bookapi.dto.book_index.BookIndexRequestDto;
import com.nhnacademy.bookapi.dto.book_index.BookIndexResponseDto;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.exception.BookIndexNotFoundException;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.BookIndexRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BookIndexService {

    private BookIndexRepository bookIndexRepository;
    private BookRepository bookRepository;

    public BookIndexService(BookIndexRepository bookIndexRepository, BookRepository bookRepository) {
        this.bookIndexRepository = bookIndexRepository;
        this.bookRepository = bookRepository;
    }

    public boolean addIndex(BookIndexRequestDto bookIndexRequestDto) { // 특정 책에 목차 생성
        if (bookIndexRepository.existsByBookAndSequence(bookIndexRequestDto.getBookId(), bookIndexRequestDto.getSequence())) {
            throw new BookIndexNotFoundException("Already exist");
        }
        Book book = bookRepository.findById(bookIndexRequestDto.getBookId()).orElseThrow(() -> new BookNotFoundException("Book not found"));
        bookIndexRepository.save(new BookIndex(bookIndexRequestDto.getTitle(),
                bookIndexRequestDto.getNumber(), bookIndexRequestDto.getSequence(), book));
        return true;
    }

    public List<BookIndexResponseDto> getIndicesByBook(Long bookId) { // 특정 책의 모든 목차 조회
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book not found"));
        Optional<List<BookIndex>> bookIndex = bookIndexRepository.findByBook(book);
        if (bookIndex.isEmpty()) {
            throw new BookNotFoundException("BookIndex not found");
        }
        List<BookIndexResponseDto> result = new ArrayList<>();
        for (BookIndex bookIndexItem : bookIndex.get()) {
            result.add(new BookIndexResponseDto(bookIndexItem.getTitle(), bookIndexItem.getNumber(), bookIndexItem.getSequence()));
        }
        return result;
    }

    //수정하려는 book_id를 이용해 어떤 목차를 수정할 것인지
    public boolean updateIndex(BookIndexRequestDto bookIndexRequestDto) { // bookIndexRequestDto에는 수정하려는 bookId와 sequence를 통해 bookIndex 수정
        Book book = bookRepository.findById(bookIndexRequestDto.getBookId()).orElseThrow(() -> new BookNotFoundException("Book not found"));
        Optional<BookIndex> bookIndex = bookIndexRepository.findByBookAndSequence(book, bookIndexRequestDto.getSequence());
        if (bookIndex.isEmpty()) {
            throw new BookIndexNotFoundException("BookIndex not found");
        }
        bookIndex.get().setTitle(bookIndexRequestDto.getTitle());
        bookIndex.get().setNumber(bookIndexRequestDto.getNumber());
        bookIndexRepository.save(bookIndex.get());
        return true;
    }

    public boolean deleteIndex(Long indexId) { // 삭제 하려는 indexId를 파라미터로 받아서 해당 컬럼 삭제
        if (!bookIndexRepository.existsById(indexId)) {
            throw new BookIndexNotFoundException("BookIndex not found");
        }
        bookIndexRepository.deleteById(indexId);
        return true;
    }



}
