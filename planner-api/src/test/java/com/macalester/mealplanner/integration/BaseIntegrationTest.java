package com.macalester.mealplanner.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macalester.mealplanner.BasePostgresContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest extends BasePostgresContainer {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
}
