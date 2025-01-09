package com.nhnacademy.bookapi.service.bookcreator;

import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDTO;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorResponseDTO;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import com.nhnacademy.bookapi.exception.BookCreatorNotFoundException;
import com.nhnacademy.bookapi.repository.BookCreatorMapRepository;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookCreatorService {

    private final BookCreatorRepository bookCreatorRepository;
    private final BookCreatorMapRepository bookCreatorMapRepository;

    public BookCreatorResponseDTO BookCreatorListByBookId(long bookId) {

        List<BookCreator> creatorByBookId = bookCreatorRepository.findCreatorByBookId(bookId);

        BookCreatorResponseDTO bookCreatorResponseDTO = new BookCreatorResponseDTO(creatorByBookId);

        return bookCreatorResponseDTO;
    }

    public List<BookCreatorDTO> bookCreatorList(long bookId) {

        List<BookCreatorDTO> creatorByBookId = bookCreatorRepository.findCreatorByBookId(bookId).stream().map(
            bookCreator -> new BookCreatorDTO(bookCreator.getId(), bookCreator.getName(), bookCreator.getRole().name())
        ).toList();


        return creatorByBookId;
    }
    public BookCreator getBookCreatorByCreatorId(long creatorId) {
        return bookCreatorRepository.findById(creatorId).orElse(null);
    }

    public void saveBookCreator(BookCreator bookCreator, BookCreatorMap bookCreatorMap) {
        bookCreatorRepository.save(bookCreator);
        bookCreatorMapRepository.save(bookCreatorMap);
    }

    public void saveBookCreatorMap(BookCreatorMap bookCreatorMap) {
        bookCreatorMapRepository.save(bookCreatorMap);
    }

    public void deleteBookCreatorMap(long bookId) {
        bookCreatorMapRepository.deleteAllByBookId(bookId);
    }

    public BookCreator getBookCreatorByName(String name){
        return bookCreatorRepository.findCreatorByName(name).orElse(null);
    }
}
