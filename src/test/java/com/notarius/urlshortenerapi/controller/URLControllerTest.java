package com.notarius.urlshortenerapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notarius.urlshortenerapi.service.URLService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.notarius.urlshortenerapi.model.URLMap;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class URLControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private URLService urlService;

    @BeforeEach
    public void setup() {
        when(urlService.getAllURLs()).thenReturn(Collections.singletonList(new URLMap("shortUrl", "fullUrl")));
        Mockito.when(urlService.shortenURL(anyString())).thenReturn("https://notarius.ca/abcd1234");
        Mockito.when(urlService.expandURL("https://notarius.ca/abcd1234")).thenReturn("https://gmail.com");

    }

    @Test
    public void testShortenURL() throws Exception {
        // Given
        String fullURL = "https://gmail.com";
        String shortenedURL = "https://notarius.ca/abcd1234";
        when(urlService.shortenURL(fullURL)).thenReturn(shortenedURL);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("fullURL", fullURL);

        // When & Then
        mockMvc.perform(post("/api/shortenurl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortURL").value(shortenedURL));
    }

    @Test
    public void testExpandURL() throws Exception {
        // Given
        String givenShortURL = "https://notarius.ca/abcd1234";
        String fullURL = "https://gmail.com";
        when(urlService.expandURL(givenShortURL)).thenReturn(fullURL);

        // When & Then
        mockMvc.perform(get("/api/original")
                        .param("shortURL", givenShortURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullURL").value(fullURL));
    }

    @Test
    public void testGetAllURLs() throws Exception {
        // Given
        List<URLMap> mockURLs = Arrays.asList(
                new URLMap("https://gmail.com", "https://notarius.ca/abc123"),
                new URLMap("https://github.com", "https://notarius.ca/def456")
        );
        when(urlService.getAllURLs()).thenReturn(mockURLs);

        // When & Then
        mockMvc.perform(get("/api/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullURL").value("https://gmail.com"))
                .andExpect(jsonPath("$[0].shortURL").value("https://notarius.ca/abc123"))
                .andExpect(jsonPath("$[1].fullURL").value("https://github.com"))
                .andExpect(jsonPath("$[1].shortURL").value("https://notarius.ca/def456"));
    }

    //Integration Tests

    @Test
    public void testShortenURLIntegration() throws Exception {
        String fullURL = "https://gmail.com";
        Map<String, String> requestBody = Map.of("fullURL", fullURL);

        mockMvc.perform(post("/api/shortenurl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortURL").exists());
    }

    @Test
    public void testExpandURLIntegration() throws Exception {
        String givenShortURL = "https://notarius.ca/abcd1234";
        mockMvc.perform(get("/api/original")
                        .param("shortURL", givenShortURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullURL").value("https://gmail.com"));
    }

    @Test
    public void testGetAllURLsIntegration() throws Exception {
        mockMvc.perform(get("/api/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }


}
