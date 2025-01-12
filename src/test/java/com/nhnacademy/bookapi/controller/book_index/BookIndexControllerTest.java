package com.nhnacademy.bookapi.controller.book_index;


import com.nhnacademy.bookapi.dto.book_index.BookIndexRequestDto;
import com.nhnacademy.bookapi.dto.book_index.BookIndexResponseDto;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.service.book_index.BookIndexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookIndexControllerTest {

    @InjectMocks
    private BookIndexController bookIndexController;

    @Mock
    private BookIndexService bookIndexService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookIndexController).build();
    }

    @Test
    void testAddIndex() throws Exception {
        BookIndexRequestDto requestDto = BookIndexRequestDto.builder().bookId(1L).indexText("Updated Chapter").build();


        mockMvc.perform(post("/admin/book-indices")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"bookId\":1,\"indexText\":\"Updated Chapter\"}"))
            .andExpect(status().isCreated());

        verify(bookIndexService, times(1)).addIndex(any(BookIndexRequestDto.class));
    }

    @Test
    void testGetIndicesByBook() throws Exception {
        BookIndexRequestDto requestDto = BookIndexRequestDto.builder().bookId(1L).indexText("Updated Chapter").build();

        BookIndex bookIndex = BookIndex.builder().id(1L).indexes("Updated Chapter").build();

        when(bookIndexService.getBookIndex(1L)).thenReturn(bookIndex);

        mockMvc.perform(get("/book-indices/{bookId}", 1L))
            .andExpect(status().isOk());
    }

    @Test
    void testUpdateIndex() throws Exception {
        BookIndexRequestDto requestDto = BookIndexRequestDto.builder().bookId(1L).indexText("Updated Chapter").build();


        mockMvc.perform(put("/admin/book-indices")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"bookId\":1,\"indexText\":\"Updated Chapter\"}"))
            .andExpect(status().isOk());

        verify(bookIndexService, times(1)).updateIndex(any(BookIndexRequestDto.class));
    }

    @Test
    void testDeleteIndex() throws Exception {
        mockMvc.perform(delete("/admin/book-indices/{bookId}", 1L)
                .param("sequence", "1"))
            .andExpect(status().isNoContent());

        verify(bookIndexService, times(1)).deleteIndex(1L);
    }
}
