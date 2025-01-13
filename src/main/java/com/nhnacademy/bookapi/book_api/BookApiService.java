package com.nhnacademy.bookapi.book_api;

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


    private String BOOK = "Book";

    public BookApiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    public JsonNode getBookList(String bookType,String searchTarget, int start, int max) throws Exception{
//
//        String url =  "http://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey="+apiKey+"&QueryType="+ bookType +"&MaxResults="+max+"&start="+start+"&SearchTarget="+ searchTarget +"&Cover=Big&output=js&Version=20131101";
//
//        // REST API 호출
//        String jsResponse = restTemplate.getForObject(url, String.class);
//
//        // JSON 응답을 파싱
//        JsonNode rootNode = objectMapper.readTree(jsResponse);
//
//        // 원하는 데이터 반환
//        return rootNode.path("item");
//    }
//
//    public JsonNode getEditorChoiceBookList(String bookType,String searchTarget, int start, int max, int categoryId) throws Exception{
//
//        String url =  "http://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey="+apiKey+"&QueryType="+ bookType +"&MaxResults="+max+"&start="+start+"&SearchTarget="+ searchTarget+ "&Cover=Big&CategoryId="+ categoryId +"&output=js&Version=20131101";
//
//        // REST API 호출
//        String jsResponse = restTemplate.getForObject(url, String.class);
//
//        // JSON 응답을 파싱
//        JsonNode rootNode = objectMapper.readTree(jsResponse);
//
//        // 원하는 데이터 반환
//        return rootNode.path("item");
//
//    }

    public JsonNode getBook(String isbn) throws Exception{

        String url = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?ttbkey="+ apiKey+"&itemIdType=ISBN13&ItemId="+isbn+"&output=JS&Version=20131101&"
            + "OptResult=Toc";

        String jsResponse = restTemplate.getForObject(url, String.class);
        JsonNode rootNode = objectMapper.readTree(jsResponse);

        return rootNode.path("item");
    }


}
