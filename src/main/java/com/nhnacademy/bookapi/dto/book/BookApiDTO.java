package com.nhnacademy.bookapi.dto.book;

import com.nhnacademy.bookapi.dto.book_type.BookTypeDTO;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDTO;
import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import com.nhnacademy.bookapi.entity.Role;
import com.nhnacademy.bookapi.entity.Type;
import com.nhnacademy.bookapi.mapper.RoleMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BookApiDTO {

    private String title;
    private String isbn;
    private List<CategoryDTO> categories;
    private List<BookTypeDTO> bookTypes;
    private List<BookCreatorDTO> authors;
    private LocalDate publishedDate;
    private String description;
    private int regularPrice;
    private int salePrice;
    private List<String> coverImage;
    private int stock;
    private int page;
    private String publisherName;


    public BookApiDTO(String title, String isbn, LocalDate publishedDate, String description,
        int regularPrice, int salePrice, List<String> coverImage, int stock, int page,
        String publisherName) {
        this.title = title;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
        this.description = description;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.coverImage = coverImage;
        this.stock = stock;
        this.page = page;
        this.publisherName = publisherName;
    }

    public void createCategoryParse(String category) {
        String[] categories = category.split(">");
        int level = 0;
        List<CategoryDTO> categoriesList = new ArrayList<>();
        for (String s : categories) {
            CategoryDTO categoryDTO = new CategoryDTO(s.trim(), level++);
            categoriesList.add(categoryDTO);
        }
        this.categories = categoriesList;
    }

    public void createBookTypeParse(int rank) {
        BookTypeDTO bookTypeDTO = new BookTypeDTO(
            "BOOK", rank
        );
        this.bookTypes = List.of(bookTypeDTO);
    }

    public void createAuthorParse(String author) {
        List<BookCreatorDTO> bookTypesList = new ArrayList<>();
        String[] split = author.split("\\),");
        for (String s : split) {
            s = s.trim(); // 공백 제거
            Role role = null;
            String roleName = null;
            if(s.contains("(") && s.contains(")")) {
                roleName = s.substring(s.indexOf("(") + 1, s.indexOf(")")).trim();
            }
            role = RoleMapper.getRole(roleName);

            String[] nameList = s.split(", ");
            for (String name : nameList) {
                if(name.contains("(")) {
                    name = name.substring(0, name.indexOf("(")).trim();
                    BookCreatorDTO bookCreatorDTO = new BookCreatorDTO(name, role.name());
                    bookTypesList.add(bookCreatorDTO);
                }
            }
        }
        this.authors = bookTypesList;
    }


}
