package com.nhnacademy.bookapi.controller;

import com.nhnacademy.bookapi.service.book.BookApiSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/aladinApi")
public class BookApiController {

    private final BookApiSaveService bookApiSaveService;


    /*bookType = ItemNewAll:신간, Bestseller:베스트셀러
       http://localhost:8080/aladin?bookType=ItemNewAll&searchTarget=Book&start=1&max=100
       searchTarget = eBook, Book, Foreign(외국도서),  */
    @GetMapping
    public ResponseEntity<String> aladinApi(@RequestParam String bookType, String searchTarget, int start, int max) throws Exception {
        bookApiSaveService.aladinApiSaveBook(bookType, searchTarget, start, max);


        return ResponseEntity.ok().body("성공");
    }
    //http://localhost:8080/aladinApi/EditorChoice?bookType=ItemEditorChoice&searchTarget=Book&start=1&max=100&categoryId=1
    @GetMapping("/EditorChoice")
    public ResponseEntity<String> aladinEditorChoiceSave(@RequestParam String bookType,
        @RequestParam String searchTarget,
        @RequestParam int start,
        @RequestParam int max,
        @RequestParam int categoryId) throws Exception {
        bookApiSaveService.aladinApiEditorChoiceSaveBook(bookType, searchTarget, start, max, categoryId);

        return ResponseEntity.ok().body("성공");
    }
    //단건조회 http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx?ttbkey=[TTBKey]&itemIdType=ISBN&ItemId=[도서의ISBN]&output=js&Version=20131101

}
