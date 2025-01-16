package com.nhnacademy.bookapi.controller.tag;

import com.nhnacademy.bookapi.dto.tag.TagRequestDto;
import com.nhnacademy.bookapi.dto.tag.TagResponseDto;
import com.nhnacademy.bookapi.service.tag.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "태그 추가", description = "새로운 태그를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "태그 추가 성공"),
            @ApiResponse(responseCode = "409", description = "태그가 이미 존재함")
    })
    @PostMapping("/admin/tags")
    public ResponseEntity<Void> addTag(@Valid @RequestBody TagRequestDto tagRequestDto) {
        tagService.addTag(tagRequestDto);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "태그 삭제", description = "태그를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "태그 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
    })
    @DeleteMapping("/admin/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모든 태그 조회", description = "모든 태그를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모든 태그 조회 성공"),
            @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
    })
    @GetMapping("/admin/tags")
    public ResponseEntity<Page<TagResponseDto>> getAllTags(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "24") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TagResponseDto> tags = tagService.getAllTags(pageable);
        return ResponseEntity.ok(tags);
    }
}
