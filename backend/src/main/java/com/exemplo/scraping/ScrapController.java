package com.exemplo.scraping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

@RestController
public class ScrapController {

    @GetMapping("/scrap")
    public List<Vaga> scrap(@RequestParam String termo) {

        termo = termo.replace(" ", "+");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new",
                "--disable-gpu",
                "--disable-software-rasterizer",
                "--window-size=1920,1080",
                "--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);

        try {
            return BneScraper.buscar(driver, termo);

        } finally {
            driver.quit();
        }
    }
}
