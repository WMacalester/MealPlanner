package com.macalester.mealplanner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatusControllerTest {

    @Autowired
    private StatusController statusController;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenStatusController_whenEndpointCalled_returnsMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/status/"))
                .andExpect(status().isOk())
                .andExpect(
                        result -> assertEquals("Server is running", result.getResponse().getContentAsString())
                );
    }
}