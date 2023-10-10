package com.notarius.urlshortenerapi.model;

import jakarta.persistence.*;

@Entity
public class URLMap {

    public URLMap(String fullURL, String shortURL) {
        this.fullURL = fullURL;
        this.shortURL = shortURL;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullURL;

    @Column(unique = true, nullable = false)
    private String shortURL;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullURL() {
        return fullURL;
    }

    public void setFullURL(String fullURL) {
        this.fullURL = fullURL;
    }

    public String getShortURL() {
        return shortURL;
    }

    public void setShortURL(String shortURL) {
        this.shortURL = shortURL;
    }

    public URLMap() {
    }

}
