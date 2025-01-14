package com.nhnacademy.bookapi.repository.tag;

import com.nhnacademy.bookapi.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagRepositoryTest {

    @Mock
    private TagRepository tagRepository;

    private String name;

    @BeforeEach
    void setUp() {
        this.name = "Test Tag";
    }

    @Test
    @DisplayName("existsByName - 태그의 이름을 이용한 태그 단건 조회 exist")
    void existsByNameExists() {
        when(tagRepository.existsByName(name)).thenReturn(true);

        boolean result = tagRepository.existsByName(name);

        assertThat(result).isTrue();
        verify(tagRepository, times(1)).existsByName(name);
    }

    @Test
    @DisplayName("existsByName - 태그의 이름을 이용한 태그 단건 조회 not exist")
    void existsByNameNotExists() {
        when(tagRepository.existsByName(name)).thenReturn(false);

        boolean result = tagRepository.existsByName(name);

        assertThat(result).isFalse();
        verify(tagRepository, times(1)).existsByName(name);
    }

    @Test
    @DisplayName("findTagNameByBookId - 도서 아이디를 이용한 태그 이름들 조회 not empty")
    void findTagNameByBookIdNotEmpty() {
        Long bookId = 1L;
        List<String> notEmptyValues = Arrays.asList("1", "2", "3");
        when(tagRepository.findTagNameByBookId(bookId)).thenReturn(notEmptyValues);
        List<String> result = tagRepository.findTagNameByBookId(bookId);
        assertThat(result).hasSameSizeAs(notEmptyValues);
        assertThat(result.getFirst()).isEqualTo(notEmptyValues.getFirst());
        assertThat(result).containsExactly("1", "2", "3");
        verify(tagRepository, times(1)).findTagNameByBookId(bookId);
    }

    @Test
    @DisplayName("findTagNameByBookId - 도서 아이디를 이용한 태그 이름들 조회 empty")
    void findTagNameByBookIdEmpty() {
        Long bookId = 1L;
        List<String> emptyValues = Collections.emptyList();
        when(tagRepository.findTagNameByBookId(bookId)).thenReturn(emptyValues);
        List<String> result = tagRepository.findTagNameByBookId(bookId);
        assertThat(result).isEmpty();
        verify(tagRepository, times(1)).findTagNameByBookId(bookId);
    }
}
