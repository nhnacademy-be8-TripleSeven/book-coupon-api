package com.nhnacademy.bookapi.service.book_type;

import com.nhnacademy.bookapi.dto.book_type.BookTypeDTO;
import com.nhnacademy.bookapi.entity.BookType;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.BookTypeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookTypeServiceImpl {


    private final BookTypeRepository bookTypeRepository;

    public List<BookTypeDTO> getUpdateBookTypeList(long bookId){
        return bookTypeRepository.findByBookId(bookId).stream().map(bookType -> new BookTypeDTO(
            bookType.getId(),
            bookType.getRanks(),
            bookType.getTypes(),
            bookType.getBook().getId()
        )).toList();
    }
}
