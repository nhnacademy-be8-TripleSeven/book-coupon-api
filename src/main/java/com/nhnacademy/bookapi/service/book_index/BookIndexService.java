package com.nhnacademy.bookapi.service.book_index;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.repository.BookIndexRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookIndexService {

    private BookIndexRepository bookIndexRepository;

    public BookIndexService(BookIndexRepository bookIndexRepository) {
        this.bookIndexRepository = bookIndexRepository;
    }

    public void addIndex(Book book, String title, int number, int sequence) { // 특정 책에 목차 생성
        bookIndexRepository.save(new BookIndex(title, number, sequence, book));
    }

    public List<BookIndex> getIndicesByBook(Book book) { // 특정 책의 모든 목차 조회
        return bookIndexRepository.findByBook(book);
    }

    @Transactional
    //수정하려는 book_id를 이용해 어떤 목차를 수정할 것인지
    public void updateIndex(Long indexId, String newTitle, int newNumber, int newSequence) {

        BookIndex bookIndex = bookIndexRepository.findById(indexId).get(); // 수정하려는 pk인 indexId를 받은 후 객체로 만듦.
        bookIndex.setTitle(newTitle);
        bookIndex.setNumber(newNumber);
        bookIndex.setSequence(newSequence);
        //bookIndexRepository.save(bookIndex);
    }

    public void deleteIndex(Long indexId) { // 삭제 하려는 indexId를 파라미터로 받아서 해당 컬럼 삭제
        bookIndexRepository.deleteById(indexId);
    }
}
