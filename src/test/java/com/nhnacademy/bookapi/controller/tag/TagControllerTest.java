package com.nhnacademy.bookapi.controller.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.config.GlobalExceptionHandler;
import com.nhnacademy.bookapi.dto.tag.TagRequestDto;
import com.nhnacademy.bookapi.dto.tag.TagResponseDto;
import com.nhnacademy.bookapi.exception.TagAlreadyExistException;
import com.nhnacademy.bookapi.exception.TagNotFoundException;
import com.nhnacademy.bookapi.service.tag.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TagController tagController;

    @Mock
    private TagService tagService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.tagService = mock(TagService.class);

        TagController tagController = new TagController(tagService);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(tagController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /admin/tags - 태그 추가")
    class AddTag {
        @Test
        @DisplayName("태그 추가 성공 시, 201(Created)")
        void testAddTagSuccess() throws Exception {
            TagRequestDto requestDto = new TagRequestDto();
            requestDto.setName("new-tag");

            doNothing().when(tagService).addTag(any(TagRequestDto.class));

            mockMvc.perform(post("/admin/tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("이미 존재하는 태그로 인한 TagAlreadyExistException -> 409 Conflict")
        void testTagAlreadyExistException() throws Exception {
            TagRequestDto requestDto = new TagRequestDto();
            requestDto.setName("duplicate-tag");

            doThrow(new TagAlreadyExistException("duplicate-tag already exists"))
                    .when(tagService).addTag(any(TagRequestDto.class));

            mockMvc.perform(post("/admin/tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.statusCode").value(409))
                    .andExpect(jsonPath("$.message").value("duplicate-tag already exists"))
                    .andExpect(jsonPath("$.requestPath").value("/admin/tags"))
                    .andExpect(jsonPath("$.localDateTime").exists());
        }
    }

    @Nested
    @DisplayName("DELETE /admin/tags/{tagId} - 태그 삭제")
    class DeleteTag {
        @Test
        @DisplayName("태그 삭제 성공 시, 204 NoContent")
        void testDeleteTagSuccess() throws Exception {
            Long tagId = 1L;
            doNothing().when(tagService).deleteTag(tagId);

            mockMvc.perform(delete("/admin/tags/{tagId}", tagId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 태그 삭제 시, TagNotFoundException -> 404 NotFound")
        void testDeleteTagNotFound() throws Exception {
            Long tagId = 999L;
            doThrow(new TagNotFoundException("TagID : " + tagId + " does not exist"))
                    .when(tagService).deleteTag(tagId);

            mockMvc.perform(delete("/admin/tags/{tagId}", tagId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /admin/tags - 모든 태그 조회")
    class GetAllTags {
        @Test
        @DisplayName("모든 태그 조회 성공 시, 200 OK + TagResponseDto 리스트")
        void testGetAllTagsSuccess() throws Exception {
            int page = 0;
            int size = 24;
            String sortField = "name";
            String sortDirection = "asc";

            Sort sort = Sort.by(Sort.Direction.ASC, sortField);
            Pageable pageable = PageRequest.of(page, size, sort);
            List<TagResponseDto> content = List.of(
                    new TagResponseDto(1L, "tag1"),
                    new TagResponseDto(2L, "tag2")
            );
            Page<TagResponseDto> tagPage = new PageImpl<>(content, pageable, content.size());

            when(tagService.getAllTags(eq(pageable))).thenReturn(tagPage);

            mockMvc.perform(get("/admin/tags")
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size))
                            .param("sortField", sortField)
                            .param("sortDirection", sortDirection))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].name").value("tag1"))
                    .andExpect(jsonPath("$.content[1].id").value(2L))
                    .andExpect(jsonPath("$.content[1].name").value("tag2"));
        }

        @Test
        @DisplayName("태그가 없어도 200 OK + 빈 페이지")
        void testGetAllTagsEmpty() throws Exception {
            int page = 0;
            int size = 24;
            String sortField = "name";
            String sortDirection = "asc";

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortField));
            Page<TagResponseDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(tagService.getAllTags(eq(pageable))).thenReturn(emptyPage);

            mockMvc.perform(get("/admin/tags")
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size))
                            .param("sortField", sortField)
                            .param("sortDirection", sortDirection))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("서비스에서 TagNotFoundException 발생 시, 404 NotFound")
        void testGetAllTagsNotFound() throws Exception {
            doThrow(new TagNotFoundException("No tags found"))
                    .when(tagService).getAllTags(any(PageRequest.class));

            mockMvc.perform(get("/admin/tags"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void testGetAllTags() throws Exception {
            int page = 0;
            int size = 24;
            String sortField = "name";
            String sortDirection = "asc";
            Sort sort = Sort.by(Sort.Direction.ASC, sortField);
            Pageable pageable = PageRequest.of(page, size, sort);
            ResponseEntity<Page<TagResponseDto>> response = tagController.getAllTags(page, size, sortField, sortDirection);

            Assertions.assertEquals(200, response.getStatusCodeValue());
        }
    }
}
