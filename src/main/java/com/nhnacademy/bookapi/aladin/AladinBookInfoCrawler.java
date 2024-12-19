package com.nhnacademy.bookapi.aladin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AladinBookInfoCrawler {

    public static Map<String, String> crawlAladinBookInfo(String bookUrl) {
        Map<String, String> bookInfo = new HashMap<>();
        bookInfo.put("목차", "목차를 찾을 수 없습니다.");
        bookInfo.put("상세 이미지 URL", "이미지 컨테이너를 찾을 수 없습니다.");

        try {
            Document doc = Jsoup.connect(bookUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                .header("Accept-Language", "ko-KR,ko;q=0.9")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Connection", "keep-alive")
                .referrer("https://www.google.com")
                .timeout(5000) // 타임아웃 추가
                .get();

            // 목차 크롤링
            Element tocSection = doc.selectFirst("div#div_TOC_Short p");
            if (tocSection != null) {
                bookInfo.put("목차", tocSection.text());
            }

            // 이미지 URL 크롤링
            Element imgTag = doc.selectFirst("div.Ere_MB20.Ere_conts_img img");
            if (imgTag != null && imgTag.hasAttr("src")) {
                String imageUrl = imgTag.attr("src");
                if (imageUrl.startsWith("//")) {
                    imageUrl = "https:" + imageUrl;  // 프로토콜 추가
                }
                System.out.println("상세 이미지 URL: " + imageUrl);
            }


        } catch (IOException e) {
            System.out.println("Failed to retrieve the page: " + e.getMessage());
        }

        return bookInfo;
    }

    public static void main(String[] args) {
        String bookUrl = "https://www.aladin.co.kr/shop/wproduct.aspx?ISBN=9788937445910";
        Map<String, String> bookInfo = crawlAladinBookInfo(bookUrl);

        System.out.println("\n목차:\n" + bookInfo.get("목차"));
        System.out.println("\n상세 이미지 URL:\n" + bookInfo.get("상세 이미지 URL"));
    }
}
