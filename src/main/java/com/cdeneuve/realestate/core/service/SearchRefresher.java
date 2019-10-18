package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.model.RefreshAttempt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;

@Slf4j
@Service
public class SearchRefresher {
    private static final String searchUrl = "https://www.immobilienscout24.de/Suche/S-2/P-1/Wohnung-Miete/Bayern/Muenchen/-/2,00-/-/EURO--1100,00/-/-/-/true";

    private LinkedList<RefreshAttempt> refreshAttempts = new LinkedList<>();

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    public SearchProcessor searchProcessor;

    public void refreshSearch() {
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, null, String.class);
            refreshAttempts.add(new RefreshAttempt(responseEntity.getStatusCode()));
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                searchProcessor.processSearchResults(responseEntity.getBody());
            }
        } catch (Throwable throwable) {
            log.error("Error on refresh", throwable);
        }
    }

    public LinkedList<RefreshAttempt> getRefreshAttempts() {
        return refreshAttempts;
    }
}
