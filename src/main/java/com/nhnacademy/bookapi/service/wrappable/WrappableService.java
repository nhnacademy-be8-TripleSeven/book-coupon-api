//package com.nhnacademy.bookapi.service.wrappable;
//
//import com.nhnacademy.bookapi.entity.Book;
//import com.nhnacademy.bookapi.entity.Wrappable;
//import com.nhnacademy.bookapi.exception.WrappableAlreadyExistException;
//import com.nhnacademy.bookapi.exception.WrappableNotFoundException;
//import com.nhnacademy.bookapi.repository.WrappableRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class WrappableService {
//
//    private WrappableRepository wrappableRepository;
//
//    public WrappableService(WrappableRepository wrappableRepository) {
//        this.wrappableRepository = wrappableRepository;
//    }
//
//    public boolean addWrappable(Book book, boolean wrappable) {
//        if (wrappableRepository.existsByBook(book)) {
//            throw new WrappableAlreadyExistException("Book is already saved");
//        }
//        wrappableRepository.save(new Wrappable(book, wrappable));
//        return true;
//    }
//
//    public boolean updateWrappable(Book book, boolean newWrappable) {
//        if (!wrappableRepository.existsByBook(book)) {
//            throw new WrappableNotFoundException("Book is not found");
//        }
//        Wrappable wrappable = wrappableRepository.findByBook(book).get();
//        wrappable.setWrappable(newWrappable);
//        wrappableRepository.save(wrappable);
//        return true;
//    }
//
//    public boolean deleteWrappable(Book book) {
//        Optional<Wrappable> wrappable = wrappableRepository.findByBook(book);
//        if (wrappable.isEmpty()) {
//            throw new WrappableNotFoundException("Book is not found");
//        }
//        wrappableRepository.delete(wrappable.get());
//        return true;
//    }
//
//    public Wrappable getWrappable(Book book) {
//        Optional<Wrappable> wrappable = wrappableRepository.findByBook(book);
//        if (wrappable.isEmpty()) {
//            throw new WrappableNotFoundException("Book is not found");
//        }
//        return wrappable.get();
//    }
//}
package com.nhnacademy.bookapi.service.wrappable;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Wrappable;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.WrappableAlreadyExistException;
import com.nhnacademy.bookapi.exception.WrappableNotFoundException;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.WrappableRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WrappableService {

    private final WrappableRepository wrappableRepository;
    private final BookRepository bookRepository;

    public WrappableService(WrappableRepository wrappableRepository, BookRepository bookRepository) {
        this.wrappableRepository = wrappableRepository;
        this.bookRepository = bookRepository;
    }

    public boolean addWrappable(Long bookId, boolean wrappable) {
        Book book = getBook(bookId);
        if (wrappableRepository.existsByBook(book)) {
            throw new WrappableAlreadyExistException("Book is already saved");
        }
        wrappableRepository.save(new Wrappable(book, wrappable));
        return true;
    }

    public boolean updateWrappable(Long bookId, boolean newWrappable) {
        Book book = getBook(bookId);
        Wrappable wrappable = wrappableRepository.findByBook(book)
                .orElseThrow(() -> new WrappableNotFoundException("Book is not found"));
        wrappable.setWrappable(newWrappable);
        wrappableRepository.save(wrappable);
        return true;
    }

    public boolean deleteWrappable(Long bookId) {
        Book book = getBook(bookId);
        Wrappable wrappable = wrappableRepository.findByBook(book)
                .orElseThrow(() -> new WrappableNotFoundException("Book is not found"));
        wrappableRepository.delete(wrappable);
        return true;
    }

    public Wrappable getWrappable(Long bookId) {
        Book book = getBook(bookId);
        return wrappableRepository.findByBook(book)
                .orElseThrow(() -> new WrappableNotFoundException("Book is not found"));
    }

    private Book getBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
    }
}
