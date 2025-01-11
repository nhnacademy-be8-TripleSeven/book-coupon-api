package com.nhnacademy.bookapi.dto.book;

import com.nhnacademy.bookapi.dto.book_type.BookTypeDTO;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDTO;
import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@Builder
public class BookUpdateDTO {

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
    @Setter
    private List<MultipartFile> coverImage;
    @Setter
    private List<MultipartFile> detailImage;
    private int stock;
    private int page;


    public BookUpdateDTO(Long id, String title, String isbn, List<CategoryDTO> categories,
        List<BookTypeDTO> bookTypes, List<BookCreatorDTO> authors, List<String> tags,
        LocalDate publishedDate, String description, int regularPrice, int salePrice, String index,
        List<MultipartFile> coverImage, List<MultipartFile> detailImage, int stock, int page) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.categories = categories;
        this.bookTypes = bookTypes;
        this.authors = authors;
        this.tags = tags;
        this.publishedDate = publishedDate;
        this.description = description;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.index = index;
        this.coverImage = coverImage;
        this.detailImage = detailImage;
        this.stock = stock;
        this.page = page;
    }

    public BookUpdateDTO(Long id, String title, String isbn, LocalDate publishedDate,
        String description, int regularPrice, int salePrice, int stock, int page) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
        this.description = description;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.page = page;
        this.stock = stock;
    }



    public void addImage(List<MultipartFile> coverImage, List<MultipartFile> detailImage) {
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