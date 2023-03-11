package com.github.ioridazo.selenium.web;

import com.github.ioridazo.selenium.domain.DownloadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeleniumController {

    private final DownloadService service;

    public SeleniumController(final DownloadService service) {
        this.service = service;
    }

    /**
     * EDINETコードリストを取得する
     *
     * @param folderPath 保存先
     * @return ダウンロードファイル名
     */
    @GetMapping("/selenium/v1/edinetcode")
    public String edinetcode(@RequestParam(name = "path", required = false) final String folderPath) {
        if (folderPath == null) {
            return service.downloadEdinetCode();
        } else {
            return service.downloadEdinetCode(folderPath);
        }
    }
}
