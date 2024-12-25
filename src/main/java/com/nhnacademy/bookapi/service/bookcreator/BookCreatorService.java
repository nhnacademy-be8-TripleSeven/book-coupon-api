package com.nhnacademy.bookapi.service.bookcreator;

import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorResponseDTO;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.exception.BookCreatorNotFoundException;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookCreatorService {

    private final BookCreatorRepository bookCreatorRepository;

    public BookCreatorResponseDTO BookCreatorListByBookId(long bookId) {

        List<BookCreator> creatorByBookId = bookCreatorRepository.findCreatorByBookId(bookId);

        BookCreatorResponseDTO bookCreatorResponseDTO = new BookCreatorResponseDTO(creatorByBookId);

        return bookCreatorResponseDTO;
    }
}
