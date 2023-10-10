package com.notarius.urlshortenerapi.controller;

import com.notarius.urlshortenerapi.model.URLMap;
import com.notarius.urlshortenerapi.service.URLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class URLController {

    @Autowired
    private URLService urlService;

    @PostMapping("/shortenurl")
    public ResponseEntity<Map<String, String>> shortenURL(@RequestBody Map<String, String> request) {
        String fullURL = request.get("fullURL");
        if (fullURL == null || fullURL.trim().isEmpty()) {
            return new ResponseEntity<>(Map.of("error", "Invalid URL provided"), HttpStatus.BAD_REQUEST);
        }

        String shortURL = urlService.shortenURL(fullURL);
        return new ResponseEntity<>(Map.of("shortURL", shortURL), HttpStatus.CREATED);
    }

    @GetMapping("/original")
    public ResponseEntity<Map<String, String>> expandURL(@RequestParam String shortURL) {
        try {
            String fullURL = urlService.expandURL(shortURL);
            return new ResponseEntity<>(Map.of("fullURL", fullURL), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/urls")
    public ResponseEntity<List<URLMap>> getAllURLs() {
        List<URLMap> urls = urlService.getAllURLs();
        return new ResponseEntity<>(urls, HttpStatus.OK);
    }
}

