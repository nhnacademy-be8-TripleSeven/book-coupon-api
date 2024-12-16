package com.nhnacademy.bookapi.service.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BookApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Value("${aladin.api.key}")
    private String apiKey;

    @Value("${koreanbook.api.key}")
    private String koreanApi;

    private String BOOK = "Book";

    public BookApiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode getBookList(String bookType,String searchTarget) throws Exception{


        String url =  "http://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey="+apiKey+"&QueryType="+ bookType +"&MaxResults=50&start=1&SearchTarget="+ searchTarget +"&output=js&Version=20131101";

        // REST API 호출
        String jsResponse = restTemplate.getForObject(url, String.class);

        // JSON 응답을 파싱
        JsonNode rootNode = objectMapper.readTree(jsResponse);

        // 원하는 데이터 반환
        return rootNode.path("item");
    }

    public JsonNode getBook(String isbn) throws Exception{

        String url = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?ttbkey="+ apiKey+"&itemIdType=ISBN13&ItemId="+isbn+"&output=js&Version=20131101&"
            + "OptResult=ebookList,usedList,reviewList";

        String jsResponse = restTemplate.getForObject(url, String.class);

        JsonNode rootNode = objectMapper.readTree(jsResponse);

        return rootNode.path("item");
    }


    public JsonNode getBookIndex(String isbn) throws Exception{
        String url = "https://www.nl.go.kr/seoji/SearchApi.do?cert_key="+koreanApi+"&result_style=json&page_no=1&page_size=10&start_publish_date=20220509&end_publish_date=20220509";
        String jsResponse = restTemplate.getForObject(url, String.class);

        JsonNode rootNode = objectMapper.readTree(jsResponse);

        return rootNode.path("item");
    }

}
