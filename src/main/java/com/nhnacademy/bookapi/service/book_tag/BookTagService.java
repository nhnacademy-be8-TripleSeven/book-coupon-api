package com.nhnacademy.bookapi.service.book_tag;

import com.nhnacademy.bookapi.dto.book_tag.BookTagRequestDTO;
import com.nhnacademy.bookapi.dto.book_tag.BookTagResponseDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookTag;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.BookTagAlreadyExistException;
import com.nhnacademy.bookapi.exception.BookTagNotFoundException;
import com.nhnacademy.bookapi.exception.TagNotFoundException;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.BookTagRepository;
import com.nhnacademy.bookapi.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class BookTagService {

    private final BookTagRepository bookTagRepository;
    private final BookRepository bookRepository;
    private final TagRepository tagRepository;

    public BookTagService(BookTagRepository bookTagRepository, BookRepository bookRepository, TagRepository tagRepository) {
        this.bookTagRepository = bookTagRepository;
        this.bookRepository = bookRepository;
        this.tagRepository = tagRepository;
    }

    private Book getBook(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new BookNotFoundException("Book not found");
        }
        return book.get();
    }

    public Tag getTag(Long tagId) {
        Optional<Tag> tag = tagRepository.findById(tagId);
        if (tag.isEmpty()) {
            throw new TagNotFoundException("Tag not found");
        }
        return tag.get();
    }
    // 저장하려는 책 id와 태그 id를 받아 insert
    public boolean addBookTag(BookTagRequestDTO bookTagRequestDTO) {
        Book book = getBook(bookTagRequestDTO.getBookId());
        Tag tag = getTag(bookTagRequestDTO.getTagId());
        if (bookTagRepository.existsByBookAndTag(book, tag)) {
            throw new BookTagAlreadyExistException("Book tag already exist");
        }
        bookTagRepository.save(new BookTag(book, tag));
        return true;
    }
    // 삭제하고픈 책의 태그 삭제
    public boolean deleteBookTag(BookTagRequestDTO bookTagRequestDTO) {
        Book book = getBook(bookTagRequestDTO.getBookId());
        Tag tag = getTag(bookTagRequestDTO.getTagId());
        if (!bookTagRepository.existsByBookAndTag(book, tag)) {
            throw new BookTagNotFoundException("Not Exist");
        }
        Optional<BookTag> bookTag = bookTagRepository.findByBookAndTag(book, tag);
        if (bookTag.isEmpty()) {
            throw new BookTagNotFoundException("Not Exist");
        }
        bookTagRepository.delete(bookTag.get());
        return true;
    }

    public void deleteAllByBookId(Long bookId) {
        Book book = getBook(bookId);
        bookTagRepository.deleteAllByBook(book);
    }

    public List<BookTagResponseDTO> getBookTagsByBook(Long bookId) { // 특정 책의 모든 태그 조회
        Book book = getBook(bookId);
        List<BookTag> tags = bookTagRepository.findAllByBookWithTags(book);
        List<BookTagResponseDTO> bookTagResponseDTOS = new ArrayList<>();
        for (BookTag bookTag : tags) {
            bookTagResponseDTOS.add(new BookTagResponseDTO(bookTag.getBook().getId(), bookTag.getTag().getId(), bookTag.getTag().getName()));
        }
        return bookTagResponseDTOS;
    }
}
