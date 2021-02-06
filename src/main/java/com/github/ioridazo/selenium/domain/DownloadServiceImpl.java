package com.github.ioridazo.selenium.domain;

import com.github.ioridazo.selenium.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
public class DownloadServiceImpl implements SeleniumService {

    @Value("${app.uri.edinetcode}")
    public String edinetcodeUri;

    /**
     * EDINETコードリストをダウンロードする
     *
     * @return ダウンロードファイル名
     */
    public String downloadEdinetCode() {
        log.info("EDINETコードリストのダウンロード処理を開始します。\t保存先:{}", "デフォルト");
        final WebDriver webDriver = new AppConfig().webDriver();

        return download(
                webDriver,
                edinetcodeUri,
                () -> ((JavascriptExecutor) webDriver).executeScript("EEW1E62071EdinetCodeListDownloadAction('lgKbn=2&dflg=0&iflg=0&dispKbn=1')")
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
        final WebDriver customWebDriver = new AppConfig().customWebDriver(downloadFolder);

        return download(
                customWebDriver,
                edinetcodeUri,
                () -> ((JavascriptExecutor) customWebDriver).executeScript("EEW1E62071EdinetCodeListDownloadAction('lgKbn=2&dflg=0&iflg=0&dispKbn=1')")
        );
    }

    @SuppressWarnings("SameParameterValue")
    private <T> String download(final WebDriver webDriver, final String uri, final Supplier<T> download) {
        //指定したURLに遷移する
        webDriver.get(uri);

        // 最大5秒間、ページが完全に読み込まれるまで待つ
        webDriver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);

        // ダウンロードする
        download.get();

        // chromeダウンロードに移動する
        webDriver.get("chrome://downloads");

        final var javascriptExecutor = (JavascriptExecutor) webDriver;

        // 100%完了するまでダウンロードを待つ
        try {
            double percentageProgress = 0;
            Thread.sleep(1000);
            while (percentageProgress != 100) {
                percentageProgress = (Long) javascriptExecutor.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('#progress').value");
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            log.warn("", e);
            Thread.currentThread().interrupt();
        }

        // ダウンロードソースリンクのURLを取得する
        var downloadSourceLink = (String) javascriptExecutor.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('div#content #file-link').href");

        log.info("正常にダウンロードしました。\tURL:{}", downloadSourceLink);

        // Java Queryを使用してファイル名を取得する
        var fileName = (String) javascriptExecutor.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('div#content #file-link').text");

        webDriver.close();

        return fileName;
    }
}
