package com.nhnacademy.bookapi.dto.book;

import com.nhnacademy.bookapi.dto.book_type.BookTypeDTO;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDTO;
import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.entity.Publisher;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class BookCreatDTO {
    private long id;
    private String title;
    private String isbn;
    private List<CategoryDTO> categories;
    private List<BookTypeDTO> bookTypes;
    private List<BookCreatorDTO> authors;
    private LocalDate publishedDate;
    private String description;
    private int regularPrice;
    private int salePrice;
    private int page;
    private int stock;
    private String index;
    private List<MultipartFile> coverImage;
    private List<MultipartFile> detailImage;
    private String publisherName;
}
