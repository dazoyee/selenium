package com.github.ioridazo.selenuim.domain;

import com.github.ioridazo.selenium.config.AppConfig;
import com.github.ioridazo.selenium.domain.DownloadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DownloadServiceImplTest {

    private DownloadServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DownloadServiceImpl(new AppConfig());
    }

    @Nested
    class downloadEdinetCode {

        @Disabled
        @DisplayName("実際にダウンロードする")
        @Test
        void downloadEdinetCode_execute() {
            assertTrue(service.downloadEdinetCode().contains("Edinetcode_"));
        }

        @Disabled
        @DisplayName("保存先を指定して実際にダウンロードする")
        @Test
        void downloadEdinetCode_downloadFolder_execute() {
            var downloadFolder = "C:\\fundanalyzer\\company";
            assertTrue(service.downloadEdinetCode(downloadFolder).contains("Edinetcode_"));
        }
    }
}