package com.nhnacademy.bookapi.controller.wrappable;

import com.nhnacademy.bookapi.entity.Wrapper;
import com.nhnacademy.bookapi.service.wrappable.WrapperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WrapperController {

    private final WrapperService wrapperService;


    @Operation(summary = "포장 가능 상태 추가", description = "도서의 포장 가능 상태를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "포장 가능 상태 추가 성공"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @PostMapping("/admin/wrappers/{bookId}")
    public ResponseEntity<Void> addWrappable(@PathVariable Long bookId, @RequestParam boolean wrappable) {
        wrapperService.addWrappable(bookId, wrappable);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "포장 가능 상태 수정", description = "도서의 포장 가능 상태를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포장 가능 상태 수정 성공"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @PutMapping("/admin/wrappers/{bookId}")
    public ResponseEntity<Void> updateWrappable(@PathVariable Long bookId, @RequestParam boolean wrappable) {
        wrapperService.updateWrappable(bookId, wrappable);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "포장 가능 상태 삭제", description = "도서의 포장 가능 상태를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포장 가능 상태 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @DeleteMapping("/admin/wrappers/{bookId}")
    public ResponseEntity<Void> deleteWrappable(@PathVariable Long bookId) {
        wrapperService.deleteWrappable(bookId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "포장 가능 상태 조회", description = "도서의 포장 가능 상태를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포장 가능 상태 조회 성공"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @GetMapping("/wrappers/{bookId}")
    public ResponseEntity<Boolean> getWrappable(@PathVariable Long bookId) {
        boolean isWrappable = wrapperService.getWrappable(bookId);
        return ResponseEntity.ok(isWrappable);
    }
}
