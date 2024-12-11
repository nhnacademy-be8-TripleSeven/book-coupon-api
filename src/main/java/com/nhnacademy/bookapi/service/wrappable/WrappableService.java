package com.nhnacademy.bookapi.service.wrappable;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Wrappable;
import com.nhnacademy.bookapi.exception.WrappableAlreadyExistException;
import com.nhnacademy.bookapi.exception.WrappableNotFoundException;
import com.nhnacademy.bookapi.repository.WrappableRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WrappableService {

    private WrappableRepository wrappableRepository;

    public WrappableService(WrappableRepository wrappableRepository) {
        this.wrappableRepository = wrappableRepository;
    }

    public void addWrappable(Book book, boolean wrappable) {
        if (wrappableRepository.existsByBook(book)) {
            throw new WrappableAlreadyExistException("Book is already saved");
        }
        wrappableRepository.save(new Wrappable(book, wrappable));
    }

    public void updateWrappable(Book book, boolean newWrappable) {
        if (!wrappableRepository.existsByBook(book)) {
            throw new WrappableNotFoundException("Book is not found");
        }
        Wrappable wrappable = wrappableRepository.findByBook(book).get();
        wrappable.setWrappable(newWrappable);
        wrappableRepository.save(wrappable);
    }

    public void deleteWrappable(Book book) {
        Optional<Wrappable> wrappable = wrappableRepository.findByBook(book);
        if (wrappable.isEmpty()) {
            throw new WrappableNotFoundException("Book is not found");
        }
        wrappableRepository.delete(wrappable.get());
    }

    public Wrappable getWrappable(Book book) {
        Optional<Wrappable> wrappable = wrappableRepository.findByBook(book);
        if (wrappable.isEmpty()) {
            throw new WrappableNotFoundException("Book is not found");
        }
        return wrappable.get();
    }
}
