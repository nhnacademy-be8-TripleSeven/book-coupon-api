package com.nhnacademy.bookapi.elasticsearch.document;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@Setter
@Getter
@Document(indexName = "nhn24_real")
public class BookDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "custom_korean_analyzer")
    private String title;

    @Field(type = FieldType.Keyword)
    private String isbn13;

    @Field(type = FieldType.Date, name = "publishdate")
    private LocalDateTime publishDate;

    @Field(name = "regularprice")
    private Integer regularPrice;

    @Field(name = "saleprice")
    private Integer salePrice;

    @Field(type = FieldType.Long)
    private Long popularity;

    @Field(type = FieldType.Keyword)
    private List<String> categories;

    @Field(type = FieldType.Keyword, name = " bookcreator")
    private List<String> bookCreators;

    @Field(type = FieldType.Keyword, name = "publishername")
    private String publisherName;

    @Field(type = FieldType.Keyword, name = "coverurl")
    private String coverUrl;





}
