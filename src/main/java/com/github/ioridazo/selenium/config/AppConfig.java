package com.github.ioridazo.selenium.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class AppConfig {

    public WebDriver webDriver() {
        return new ChromeDriver(chromeOptions());
    }

    public WebDriver customWebDriver(final String downloadFolder) {
        final ChromeOptions options = chromeOptions();
        options.setExperimentalOption("prefs", Map.of("download.default_directory", downloadFolder));

        return new ChromeDriver(options);
    }

    @Bean
    public ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.setScriptTimeout(Duration.ofSeconds(15));
        options.setPageLoadTimeout(Duration.ofSeconds(15));
        return options;
    }
}
