package com.nhnacademy.bookapi.elasticsearch.document;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Setter
@Getter
@Document(indexName = "nhn24" )
public class BookDocument {

    @Id
    private String id;

    private String title;

    private String description;

    private String isbn13;

    @Field(name = "publishdate") // Elasticsearch 매핑 이름에 맞춤
    private LocalDateTime publishDate;

    @Field(name = "regularprice") // Elasticsearch 매핑 이름에 맞춤
    private int regularPrice;

    @Field(name = "saleprice") // Elasticsearch 매핑 이름에 맞춤
    private int salePrice;

    private int stock;

    private int page;

    @Field(name = "bestsellerrank") // Elasticsearch 매핑 이름에 맞춤
    private int bestSellerRank;

    @Field(name = "clickcount") // Elasticsearch 매핑 이름에 맞춤
    private int clickCount;

    @Field(name = "searchcount") // Elasticsearch 매핑 이름에 맞춤
    private int searchCount;

    @Field(name = "cartcount") // Elasticsearch 매핑 이름에 맞춤
    private int cartCount;

    @Field(name = "coverurl") // Elasticsearch 매핑 이름에 맞춤
    private String coverUrl;

    @Field(name = "publishername") // Elasticsearch 매핑 이름에 맞춤
    private String publisherName;

    @Field(name = "bookcreators") // Elasticsearch 매핑 이름에 맞춤
    private String bookCreators;

    private List<String> categories;

}
