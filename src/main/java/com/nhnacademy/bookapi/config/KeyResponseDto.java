package com.nhnacademy.bookapi.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class KeyResponseDto {
    private Header header;
    private Body body;

    @Getter
    @NoArgsConstructor
    public static class Body {
        private String secret; // 복호화된 기밀 데이터
    }

    @Getter
    @NoArgsConstructor
    public static class Header {
        private Integer resultCode;
        private String resultMessage;
        private boolean isSuccessful;
    }
}
