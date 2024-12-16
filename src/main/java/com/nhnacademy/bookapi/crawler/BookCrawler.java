package com.nhnacademy.bookapi.crawler;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.RequestUserAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
public class BookCrawler {
    public String[] fetchTableOfContents(String isbn) {
        WebDriverManager.chromedriver().setup();

        WebDriver driver = new ChromeDriver();
        String[] toc = new String[]{"목차를 가져올 수 없습니다."};

        try {
            String url = "https://www.aladin.co.kr/shop/wproduct.aspx?ISBN=" + isbn;
            driver.get(url);


            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10l)); // 최대 10초 대기

            // 목차 데이터가 있는 요소 찾기
            WebElement tocElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("div_TOC_Short")));

            String text = tocElement.getText();
            if (tocElement != null) {
                // 텍스트 가져오기 및 줄바꿈으로 분리
                toc = tocElement.getText().split("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // 브라우저 종료
        }

        return toc;
    }
}
