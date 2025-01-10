package com.nhnacademy.bookapi.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemDTO {
    Long bookId;
    int amount;
    int discountPrice;
    int primePrice;
}
