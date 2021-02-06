package com.github.ioridazo.selenuim.domain;

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
        service = new DownloadServiceImpl();
        service.edinetcodeUri = "https://disclosure.edinet-fsa.go.jp/E01EW/BLMainController.jsp?uji.bean=ee.bean.W1E62071.EEW1E62071Bean&uji.verb=W1E62071InitDisplay&TID=W1E62071&PID=W0EZ0001&SESSIONKEY=&lgKbn=2&dflg=0&iflg=0";
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