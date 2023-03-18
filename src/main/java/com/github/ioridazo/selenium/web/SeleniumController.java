package com.github.ioridazo.selenium.web;

import com.github.ioridazo.selenium.domain.DownloadService;
import com.github.ioridazo.selenium.exception.MaintenanceException;
import com.github.ioridazo.selenium.exception.SeleniumRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestController
@RestControllerAdvice
public class SeleniumController {

    private static final Logger log = LogManager.getLogger(SeleniumController.class);

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
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/selenium/v1/edinetcode")
    public Map<String, Object> edinetcode(@RequestParam(name = "path", required = false) final String folderPath) {
        final String filename;
        if (folderPath == null) {
            filename = service.downloadEdinetCode();
        } else {
            filename = service.downloadEdinetCode(folderPath);
        }

        return Map.of(
                "status", "ok",
                "content", Map.of(
                        "filename", filename
                )
        );
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(MaintenanceException.class)
    public Map<String, Object> serviceUnavailable(final MaintenanceException e) {
        log.warn(e);
        return Map.of(
                "status", "ng",
                "error", Map.of(
                        "message", "maintenance",
                        "body", e.getBody()
                )
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SeleniumRuntimeException.class)
    public Map<String, Object> internalServerError(final SeleniumRuntimeException e) {
        log.error(e);
        return Map.of(
                "status", "ng",
                "error", Map.of(
                        "message", e.getMessage(),
                        "body", e.getBody()
                )
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> internalServerError(final Exception e) {
        log.error(e);
        return Map.of(
                "status", "ng",
                "error", Map.of(
                        "message", e.getMessage()
                )
        );
    }
}
