package com.nhnacademy.bookapi.service.book_tag;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookTag;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.exception.BookTagAlreadyExistException;
import com.nhnacademy.bookapi.exception.BookTagNotFoundException;
import com.nhnacademy.bookapi.repository.BookTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookTagService {

    private final BookTagRepository bookTagRepository;
    public BookTagService(BookTagRepository bookTagRepository) {
        this.bookTagRepository = bookTagRepository;
    }

    public void addBookTag(Book book, Tag tag) {
        if (bookTagRepository.existsByBookAndTag(book, tag)) {
            throw new BookTagAlreadyExistException("Already Exist");
        }
        bookTagRepository.save(new BookTag(book, tag));
    }

    public void deleteBookTag(Book book, Tag tag) {
        if (!bookTagRepository.existsByBookAndTag(book, tag)) {
            throw new BookTagNotFoundException("Not Exist");
        }
        BookTag bookTag = bookTagRepository.findByBookAndTag(book, tag).get();
        bookTagRepository.delete(bookTag);
    }

    public void updateBookTag(Long id, Tag newTag) { // 삭제하려는 id를 이용해 해당 컬럼의 태그 업데이트
        if (!bookTagRepository.existsById(id)) {
            throw new BookTagNotFoundException("Not Exist");
        }
        BookTag bookTag = bookTagRepository.findById(id).get();
        bookTag.setTag(newTag);
        bookTagRepository.save(bookTag);
    }

    public List<BookTag> getBookTagsByBook(Book book) { // 특정 책의 모든 태그 조회
        if (!bookTagRepository.existsByBook(book)) {
            throw new BookTagNotFoundException("Not Exist");
        }
        return bookTagRepository.findByBook(book);
    }
}
