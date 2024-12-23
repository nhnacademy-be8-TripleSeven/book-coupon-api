package com.nhnacademy.bookapi.elasticsearch.document;



import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;


@Getter
@Document(indexName = "nhn24")
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

    private int bestSellerRank;

    private int clickCount;

    private int searchCount;

    private int cartCount;

    private String coverUrl;

    @Field(name = "publisher_name")
    private String publisherName;

    private String bookcreator;

    private List<String> categories;

}
