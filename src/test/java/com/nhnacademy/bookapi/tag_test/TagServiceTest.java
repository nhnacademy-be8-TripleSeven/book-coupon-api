package com.nhnacademy.bookapi.tag_test;

import com.nhnacademy.bookapi.dto.tag.TagRequestDto;
import com.nhnacademy.bookapi.dto.tag.TagResponseDto;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.exception.TagAlreadyExistException;
import com.nhnacademy.bookapi.exception.TagNotFoundException;
import com.nhnacademy.bookapi.repository.TagRepository;
import com.nhnacademy.bookapi.service.tag.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTag_Success() {
        TagRequestDto tagRequestDto = new TagRequestDto("programming");
        when(tagRepository.existsByName(tagRequestDto.getName())).thenReturn(false);

        assertTrue(tagService.addTag(tagRequestDto));
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void addTag_TagAlreadyExists() {
        TagRequestDto tagRequestDto = new TagRequestDto("programming");
        when(tagRepository.existsByName(tagRequestDto.getName())).thenReturn(true);

        assertThrows(TagAlreadyExistException.class, () -> tagService.addTag(tagRequestDto));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void updateTag_Success() {
        Long tagId = 1L;
        Tag existingTag = new Tag("programming");
        TagRequestDto tagRequestDto = new TagRequestDto("programming2");

        when(tagRepository.existsById(tagId)).thenReturn(true);
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));

        assertTrue(tagService.updateTag(tagId, tagRequestDto));
        assertEquals("programming2", existingTag.getName());
        verify(tagRepository, times(1)).save(existingTag);
    }

    @Test
    void updateTag_TagNotFound() {
        Long tagId = 1L;
        TagRequestDto tagRequestDto = new TagRequestDto("programming2");

        when(tagRepository.existsById(tagId)).thenReturn(false);

        assertThrows(TagNotFoundException.class, () -> tagService.updateTag(tagId, tagRequestDto));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void deleteTagById_Success() {
        Long tagId = 1L;
        when(tagRepository.existsById(tagId)).thenReturn(true);

        assertTrue(tagService.deleteTag(tagId));
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    void deleteTagById_TagNotFound() {
        Long tagId = 1L;
        when(tagRepository.existsById(tagId)).thenReturn(false);

        assertThrows(TagNotFoundException.class, () -> tagService.deleteTag(tagId));
        verify(tagRepository, never()).deleteById(tagId);
    }

    @Test
    void getTagById_Success() {
        Long tagId = 1L;
        Tag tag = new Tag("programming");
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        TagResponseDto tagResponseDto = tagService.getTagById(tagId);
        assertEquals(tag.getName(), tagResponseDto.getName());
        assertEquals(tag.getId(), tagResponseDto.getId());
    }

    @Test
    void getTagById_TagNotFound() {
        Long tagId = 1L;
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> tagService.getTagById(tagId));
    }

    @Test
    void getAllTags_Success() {
        List<Tag> tags = Arrays.asList(
                new Tag("programming"),
                new Tag("java"),
                new Tag("spring")
        );
        when(tagRepository.findAll()).thenReturn(tags);

        List<TagResponseDto> tagResponseDtos = tagService.getAllTags();
        assertEquals(3, tagResponseDtos.size());
        assertEquals("programming", tagResponseDtos.get(0).getName());
        assertEquals("java", tagResponseDtos.get(1).getName());
        assertEquals("spring", tagResponseDtos.get(2).getName());
    }

//    @Test
//    void getTagByName_Success() {
//        String tagName = "programming";
//        Tag tag = new Tag(tagName);
//        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));
//
//        TagResponseDto tagResponseDto = tagService.getTagByName(tagName);
//        assertEquals(tag.getName(), tagResponseDto.getName());
//    }

//    @Test
//    void getTagByName_TagNotFound() {
//        String tagName = "programming";
//        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());
//
//        assertThrows(TagNotFoundException.class, () -> tagService.getTagByName(tagName));
//    }
}
