package com.nhnacademy.bookapi.dto.book;

import com.nhnacademy.bookapi.dto.book_type.BookTypeDTO;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDTO;
import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class BookDTO {

    private Long id;
    private String title;
    private String isbn;
    private List<CategoryDTO> categories;
    private List<BookTypeDTO> bookTypes;
    private List<BookCreatorDTO> authors;
    private List<String> tags;
    private LocalDate publishedDate;
    private String description;
    private int regularPrice;
    private int salePrice;
    private String index;
    private List<String> coverImage;
    private List<String> detailImage;
    private int stock;
    private int page;

    public BookDTO(Long id, String title, String isbn, LocalDate publishedDate, String description,
        int regularPrice, int salePrice, int stock, int page) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
        this.description = description;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.stock = stock;
        this.page = page;
    }

    public void addImage(List<String> coverImage, List<String> detailImage) {
        this.coverImage = coverImage;
        this.detailImage = detailImage;
    }


    public void addCategory(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public void addBookType(List<BookTypeDTO> bookTypes) {
        this.bookTypes = bookTypes;
    }
    public void addAuthor(List<BookCreatorDTO> authors) {
        this.authors = authors;
    }
    public void addTags(List<String> tags) {
        this.tags = tags;
    }
    public void addIndex(String index) {
        this.index = index;
    }


}
