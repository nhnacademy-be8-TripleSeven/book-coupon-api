package com.nhnacademy.bookapi.dto.book;

import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookOrderDetailResponse {

    private Long id;
    private String title;
    private int regularPrice;
    private int salePrice;
    private String coverUrl;
    private boolean wrappable;
    private List<CategoryDTO> category;

}
