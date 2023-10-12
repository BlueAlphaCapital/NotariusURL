package com.notarius.urlshortenerapi.service;

import com.notarius.urlshortenerapi.model.URLMap;
import com.notarius.urlshortenerapi.repository.URLRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class URLServiceTest {

    @InjectMocks
    private URLService urlService;

    @Mock
    private URLRepository urlRepository;

    @Test
    public void testShortenURLForExistingURL() {
        // Given
        String givenURL = "https://gmail.com";
        String shortenedURL = "https://notarius.ca/abcd1234";
        when(urlRepository.findByFullURL(givenURL)).thenReturn(Optional.of(new URLMap(givenURL, shortenedURL)));

        // When
        String result = urlService.shortenURL(givenURL);

        // Then
        assertEquals(shortenedURL, result);
        verify(urlRepository, times(1)).findByFullURL(givenURL);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    public void testShortenURLForNewURL() {
        // Given
        String givenURL = "https://github.com";
        when(urlRepository.findByFullURL(givenURL)).thenReturn(Optional.empty());
        when(urlRepository.findByShortURL(anyString())).thenReturn(Optional.empty());

        // When
        String result = urlService.shortenURL(givenURL);

        // Then
        assertNotNull(result);
        assertTrue(result.startsWith("https://notarius.ca/"));
        verify(urlRepository, times(1)).findByFullURL(givenURL);
        verify(urlRepository, atLeastOnce()).findByShortURL(anyString());
        verify(urlRepository, times(1)).save(any(URLMap.class));
    }

    @Test
    public void testExpandURLExists() {
        // Given
        String givenShortURL = "https://notarius.ca/abcd1234";
        String fullURL = "https://gmail.com";
        when(urlRepository.findByShortURL(givenShortURL)).thenReturn(Optional.of(new URLMap(fullURL, givenShortURL)));

        // When
        String result = urlService.expandURL(givenShortURL);

        // Then
        assertEquals(fullURL, result);
        verify(urlRepository, times(1)).findByShortURL(givenShortURL);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    public void testExpandURLNotFound() {
        // Given
        String givenShortURL = "https://notarius.ca/abcd1234";
        when(urlRepository.findByShortURL(givenShortURL)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> urlService.expandURL(givenShortURL));
        verify(urlRepository, times(1)).findByShortURL(givenShortURL);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    public void testGetAllURLs() {
        // Given
        List<URLMap> mockURLs = Arrays.asList(
                new URLMap("https://gmail.com", "https://notarius.ca/abc123"),
                new URLMap("https://github.com", "https://notarius.ca/def456")
        );
        when(urlRepository.findAll()).thenReturn(mockURLs);

        // When
        List<URLMap> result = urlService.getAllURLs();

        // Then
        assertEquals(2, result.size());
        assertEquals("https://gmail.com", result.get(0).getFullURL());
        assertEquals("https://notarius.ca/abc123", result.get(0).getShortURL());
        verify(urlRepository, times(1)).findAll();
        verifyNoMoreInteractions(urlRepository);
    }

}