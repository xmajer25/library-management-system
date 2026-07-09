package com.xmajer.librarymanagementsystem.integration.api;

import com.xmajer.librarymanagementsystem.integration.BaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class BookCopyApiIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
}
