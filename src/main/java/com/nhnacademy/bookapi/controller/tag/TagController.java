package com.nhnacademy.bookapi.controller.tag;

import com.nhnacademy.bookapi.dto.tag.TagRequestDto;
import com.nhnacademy.bookapi.dto.tag.TagResponseDto;
import com.nhnacademy.bookapi.service.tag.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "태그 추가", description = "새로운 태그를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "태그 추가 성공"),
            @ApiResponse(responseCode = "400", description = "태그가 이미 존재함")
    })
    @PostMapping("/admin/tags")
    public ResponseEntity<Void> addTag(@RequestBody TagRequestDto tagRequestDto) {
        tagService.addTag(tagRequestDto);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "태그 수정", description = "태그 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "태그 수정 성공"),
            @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
    })
    @PutMapping("/admin/tags/{tagId}")
    public ResponseEntity<Void> updateTag(@PathVariable Long tagId, @RequestBody TagRequestDto tagRequestDto) {
        tagService.updateTag(tagId, tagRequestDto);
        return ResponseEntity.ok().build();
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

    @Operation(summary = "태그 조회 (ID)", description = "ID를 사용하여 태그를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "태그 조회 성공"),
            @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
    })
    @GetMapping("/tags/{tagId}")
    public ResponseEntity<TagResponseDto> getTagById(@PathVariable Long tagId) {
        TagResponseDto tagResponse = tagService.getTagById(tagId);
        return ResponseEntity.ok(tagResponse);
    }

    @Operation(summary = "태그 조회 (이름)", description = "이름을 사용하여 태그를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "태그 조회 성공"),
            @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
    })
    @GetMapping("/tags/name")
    public ResponseEntity<TagResponseDto> getTagByName(@RequestParam String name) {
        TagResponseDto tagResponse = tagService.getTagByName(name);
        return ResponseEntity.ok(tagResponse);
    }

    @Operation(summary = "모든 태그 조회", description = "모든 태그를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모든 태그 조회 성공"),
            @ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
    })
    @GetMapping("/tags")
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        List<TagResponseDto> tags = tagService.getAllTags();
        if (tags.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tags);
    }
}
