package com.github.ioridazo.selenium.web;

import com.github.ioridazo.selenium.domain.SeleniumService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeleniumController {

    private final SeleniumService service;

    public SeleniumController(final SeleniumService service) {
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
