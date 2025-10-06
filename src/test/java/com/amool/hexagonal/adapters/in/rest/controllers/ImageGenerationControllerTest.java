package com.amool.hexagonal.adapters.in.rest.controllers;

import com.amool.hexagonal.application.port.in.ImageGenerationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ImageGenerationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ImageGenerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageGenerationService imageGenerationService;

    @Test
    @DisplayName("POST /api/images/generate returns image URL")
    void generate_ReturnsImageUrl() throws Exception {
        String expectedUrl = "https://example.com/image.png";
        given(imageGenerationService.generateImageUrl(anyString())).willReturn(expectedUrl);

        String body = "{\n  \"prompt\": \"example prompt\"\n}";

        mockMvc.perform(post("/api/images/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(expectedUrl));
    }
}
