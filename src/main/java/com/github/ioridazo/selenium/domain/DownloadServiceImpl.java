package com.github.ioridazo.selenium.domain;

import com.github.ioridazo.selenium.config.AppConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

@Service
public class DownloadServiceImpl implements SeleniumService {

    private static final Logger log = LogManager.getLogger(DownloadServiceImpl.class);

    private static final String TARGET_URL = "https://disclosure2.edinet-fsa.go.jp/weee0010.aspx";

    private final AppConfig config;

    public DownloadServiceImpl(final AppConfig config) {
        this.config = config;
    }

    /**
     * EDINETコードリストをダウンロードする
     *
     * @return ダウンロードファイル名
     */
    public String downloadEdinetCode() {
        log.info("EDINETコードリストのダウンロード処理を開始します。\t保存先:{}", "デフォルト");
        WebDriverManager.chromedriver().setup();
        final WebDriver webDriver = config.webDriver();

        return download(
                webDriver,
                TARGET_URL,
                () -> webDriver.findElement(By.xpath("//*[@id='GridContainerRow_0001']/td[2]/p/span/a")).click()
        );
    }

    /**
     * EDINETコードリストを指定保存先にダウンロードする
     *
     * @param downloadFolder 保存先
     * @return ダウンロードファイル名
     */
    public String downloadEdinetCode(final String downloadFolder) {
        log.info("EDINETコードリストのダウンロード処理を開始します。\t保存先:{}", downloadFolder);
        WebDriverManager.chromedriver().setup();
        final WebDriver customWebDriver = config.customWebDriver(downloadFolder);

        return download(
                customWebDriver,
                TARGET_URL,
                () -> customWebDriver.findElement(By.xpath("//*[@id='GridContainerRow_0001']/td[2]/p/span/a")).click()
        );
    }

    @SuppressWarnings("SameParameterValue")
    private String download(final WebDriver webDriver, final String uri, final Runnable download) {
        //指定したURLに遷移する
        webDriver.get(uri);

        // ダウンロードする
        download.run();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // chromeダウンロードに移動する
        webDriver.get("chrome://downloads");

        final var javascriptExecutor = (JavascriptExecutor) webDriver;

        // Java Queryを使用してファイル名を取得する
        var fileName = (String) javascriptExecutor.executeScript(
                """
                        return document.querySelector('downloads-manager').shadowRoot
                        .querySelector('#downloadsList downloads-item').shadowRoot
                        .querySelector('div#content #file-link').text
                        """
        );

        log.info("正常にダウンロードしました。\tファイル名：{}", fileName);

        webDriver.close();

        return fileName;
    }
}
