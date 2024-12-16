package com.nhnacademy.bookapi.elasticsearch.document;


import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.entity.Image;
import com.nhnacademy.bookapi.entity.Publisher;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;


@Getter
@Setter
@Document(indexName = "books")
public class BookDocument {

    @Id
    private String id;

    private String title;
    private String description;
    private String isbn13;

    @Field(name = "publish_date")
    private LocalDate publishDate;
    @Field(name = "regular_price")
    private int regularPrice;
    @Field(name = "sale_price")
    private int salePrice;
    private int stock;
    private int page;
    @Field(name = "cover_url")
    private String coverUrl;
    @Field(name = "publisher_name")
    private String publisherName;
    @Field(name = "book_creators")
    private List<BookCreator> bookCreators;
    private List<Category> categories;

}
