package com.notarius.urlshortenerapi.repository;

import com.notarius.urlshortenerapi.model.URLMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface URLRepository extends JpaRepository<URLMap, Long> {
    Optional<URLMap> findByShortURL(String shortURL);
    Optional<URLMap> findByFullURL(String fullURL);
}

