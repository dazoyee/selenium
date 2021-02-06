package com.github.ioridazo.selenium.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class AppConfig {

    public WebDriver webDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver/chromedriver.exe");
        return new ChromeDriver();
    }

    public WebDriver customWebDriver(final String downloadFolder) {
        System.setProperty("webdriver.chrome.driver", "chromedriver/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        final var chromePref = new HashMap<String, String>();
        chromePref.put("download.default_directory", downloadFolder);
        options.setExperimentalOption("prefs", chromePref);
        return new ChromeDriver(options);
    }
}
