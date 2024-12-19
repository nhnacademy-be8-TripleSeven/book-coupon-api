import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class AladinTocSelenium {
    public static void main(String[] args) {
        // ChromeDriver 경로 설정
        System.setProperty("webdriver.chrome.driver", "/Users/jangsajang/downloads/chromedriver");

        WebDriver driver = new ChromeDriver();

        try {
            // 알라딘 도서 페이지 열기
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.get("https://www.aladin.co.kr/shop/wproduct.aspx?ISBN=9788937445910");

            // 목차 열기 버튼 클릭
            WebElement toggleButton = driver.findElement(By.id("TOC_Toggle"));
            toggleButton.click(); // 버튼 클릭하여 목차 열기

            // 목차 내용 가져오기
            WebElement tocElement = driver.findElement(By.cssSelector("div#div_TOC_Short p"));
            String tocContent = tocElement.getText(); // 목차 텍스트 가져오기
            System.out.println("목차:\n" + tocContent);

        } catch (Exception e) {
            System.out.println("오류 발생: " + e.getMessage());
        } finally {
            // 브라우저 닫기
            driver.quit();
        }
    }
}
