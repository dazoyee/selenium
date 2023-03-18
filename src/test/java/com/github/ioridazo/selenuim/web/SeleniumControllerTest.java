package com.github.ioridazo.selenuim.web;

import com.github.ioridazo.selenium.domain.DownloadService;
import com.github.ioridazo.selenium.web.SeleniumController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@SpringBootTest(classes = SeleniumController.class)
class SeleniumControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private DownloadService service;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void edinetcode() throws Exception {
        Mockito.doReturn("file name").when(service).downloadEdinetCode();

        mockMvc
                .perform(
                        get("/selenium/v1/edinetcode")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                    "status": "ok",
                                    "content": {
                                        "filename": "file name"
                                    }
                                 }
                                 """
                ))
        ;
    }

    @Test
    void edinetcode_param() throws Exception {
        Mockito.doReturn("file name").when(service).downloadEdinetCode(any());

        mockMvc
                .perform(
                        get("/selenium/v1/edinetcode")
                                .param("path", "path")
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                    "status": "ok",
                                    "content": {
                                        "filename": "file name"
                                    }
                                 }
                                 """
                ))
        ;
    }
}