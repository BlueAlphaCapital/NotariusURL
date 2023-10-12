package com.notarius.urlshortenerapi.service;

import com.notarius.urlshortenerapi.model.URLMap;
import com.notarius.urlshortenerapi.repository.URLRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class URLService {

    private static final Logger logger = LoggerFactory.getLogger(URLService.class);

    @Autowired
    private URLRepository urlRepository;

    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String BASE_URL = "https://notarius.ca/";

    public String shortenURL(String fullURL) {
        Optional<URLMap> existingPair = urlRepository.findByFullURL(fullURL);
        if (existingPair.isPresent()) {
            return existingPair.get().getShortURL();
        }

        String hash = getHash(fullURL);
        String shortEndURL = encodeBase62(hash).substring(0, 9);
        String fullShortURL = BASE_URL + shortEndURL;

        while(urlRepository.findByShortURL(fullShortURL).isPresent()) {
            hash = getHash(hash);
            shortEndURL = encodeBase62(hash).substring(0, 9);
            fullShortURL = BASE_URL + shortEndURL;
        }

        URLMap urlPair = new URLMap(fullURL, fullShortURL);
        urlRepository.save(urlPair);

        return fullShortURL;
    }

    private String getHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, hash);
            return number.toString(16);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error in getHash with input: {}", input, e);
            throw new RuntimeException("Error generating hash", e);
        }
    }

    private String encodeBase62(String hex) {
        BigInteger number = new BigInteger(hex, 16);
        StringBuilder encoded = new StringBuilder();

        while (number.signum() > 0) {
            encoded.append(BASE62.charAt(number.mod(BigInteger.valueOf(BASE62.length())).intValue()));
            number = number.divide(BigInteger.valueOf(BASE62.length()));
        }

        return encoded.reverse().toString();
    }

    public String expandURL(String shortURL) {
        Optional<URLMap> urlPair = urlRepository.findByShortURL(shortURL);
        return urlPair.map(URLMap::getFullURL).orElseThrow(() -> new IllegalArgumentException("URL not found"));
    }

    public List<URLMap> getAllURLs() {
        return urlRepository.findAll();
    }
}
