package com.nhnacademy.bookapi.service.tag;

import com.nhnacademy.bookapi.dto.tag.TagRequestDto;
import com.nhnacademy.bookapi.dto.tag.TagResponseDto;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.exception.TagAlreadyExistException;
import com.nhnacademy.bookapi.exception.TagNotFoundException;
import com.nhnacademy.bookapi.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TagServiceTest {
    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTagSuccess() {
        TagRequestDto tagRequestDto = new TagRequestDto();
        tagRequestDto.setName("NewTag");

        when(tagRepository.existsByName(tagRequestDto.getName())).thenReturn(false);

        boolean result = tagService.addTag(tagRequestDto);

        assertTrue(result);
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void testAddTagFailure() {
        TagRequestDto tagRequestDto = new TagRequestDto();
        tagRequestDto.setName("AlreadyExistTag");

        when(tagRepository.existsByName(tagRequestDto.getName())).thenReturn(true);
        assertThrows(TagAlreadyExistException.class, () -> tagService.addTag(tagRequestDto));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void testDeleteTagSuccess() {
        Long tagId = 1L;
        when(tagRepository.existsById(tagId)).thenReturn(true);
        boolean result = tagService.deleteTag(tagId);
        assertTrue(result);
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    void testDeleteTagFailure() {
        Long tagId = 1L;
        when(tagRepository.existsById(tagId)).thenReturn(false);
        assertThrows(TagNotFoundException.class, () -> tagService.deleteTag(tagId));
        verify(tagRepository, never()).deleteById(tagId);
    }

    @Test
    void testGetAllTagsPaged() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<Tag> tags = Arrays.asList(new Tag("tag1"), new Tag("tag2"));

        Page<Tag> tagPage = new PageImpl<>(tags, pageRequest, tags.size());

        when(tagRepository.findAll(pageRequest)).thenReturn(tagPage);
        Page<TagResponseDto> result = tagService.getAllTags(pageRequest);
        assertEquals(2, result.getContent().size());
        verify(tagRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testGetTagNamesByBookIdSuccess() {
        Long tagId = 1L;
        List<String> tagNames = Arrays.asList("tag1", "tag2");
        when(tagRepository.findTagNameByBookId(tagId)).thenReturn(tagNames);
        List<String> result = tagRepository.findTagNameByBookId(tagId);

        assertNotNull(result);
        assertEquals(tagNames, result);
        assertEquals(2, tagNames.size());
        assertEquals("tag1", tagNames.get(0));
        verify(tagRepository, times(1)).findTagNameByBookId(tagId);
    }

    @Test
    void testGetTagNameSuccess() {
        Long bookId = 1L;
        List<String> expectedTagNames = Arrays.asList("tag1", "tag2");

        // Mock 레포지토리 메서드 동작
        when(tagRepository.findTagNameByBookId(bookId)).thenReturn(expectedTagNames);

        // 서비스 메서드 호출
        List<String> result = tagService.getTagName(bookId);

        // 결과 검증
        assertNotNull(result);
        assertEquals(expectedTagNames, result);
        assertEquals(2, result.size());
        assertEquals("tag1", result.get(0));

        // 레포지토리 메서드 호출 검증
        verify(tagRepository, times(1)).findTagNameByBookId(bookId);
    }

    @Test
    void testGetTagNameEmpty() {
        Long bookId = 1L;

        // Mock 레포지토리 메서드 동작
        when(tagRepository.findTagNameByBookId(bookId)).thenReturn(Collections.emptyList());

        // 서비스 메서드 호출
        List<String> result = tagService.getTagName(bookId);

        // 결과 검증
        assertEquals(Collections.emptyList(), result);

        // 레포지토리 메서드 호출 검증
        verify(tagRepository, times(1)).findTagNameByBookId(bookId);
    }
}