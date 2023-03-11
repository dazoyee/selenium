package com.github.ioridazo.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SeleniumApplication {

    public static void main(String[] args) {
        // for io.github.bonigarcia.webdrivermanager
        System.setProperty("wdm.cachePath", ".cache");
        WebDriverManager.chromedriver().setup();

        SpringApplication.run(SeleniumApplication.class, args);
    }

}
