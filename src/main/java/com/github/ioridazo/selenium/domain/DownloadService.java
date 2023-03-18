package com.github.ioridazo.selenium.domain;

import com.github.ioridazo.selenium.config.AppConfig;
import com.github.ioridazo.selenium.exception.MaintenanceException;
import com.github.ioridazo.selenium.exception.SeleniumRuntimeException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

@Service
public class DownloadService {

    private static final Logger log = LogManager.getLogger(DownloadService.class);

    private static final String TARGET_URL = "https://disclosure2.edinet-fsa.go.jp/weee0010.aspx";

    private final AppConfig config;

    public DownloadService(final AppConfig config) {
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
        return download(config.webDriver());
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
        return download(config.customWebDriver(downloadFolder));
    }

    @SuppressWarnings("SameParameterValue")
    private String download(final WebDriver webDriver) {
        //指定したURLに遷移する
        webDriver.get(TARGET_URL);

        // ダウンロードする
        try {
            webDriver.findElement(By.xpath("//*[@id='GridContainerRow_0001']/td[2]/p/span/a")).click();
        } catch (final NoSuchElementException e) {
            final String bodyText = webDriver.findElement(By.xpath("//*[@id='TABLECONTENT_MPAGE']")).getText();
            if (bodyText.contains("システムメンテナンス")) {
                throw new MaintenanceException(e, bodyText);
            } else {
                throw new SeleniumRuntimeException(e, bodyText);
            }
        }

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
