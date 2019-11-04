package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.model.ErrorNotification;
import com.cdeneuve.realestate.core.model.RefreshAttempt;
import com.cdeneuve.realestate.core.notification.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;

@Slf4j
@Service
public class SearchRefresher {
    private static final String searchUrl = "https://www.immobilienscout24.de/Suche/S-2/Wohnung-Miete/Bayern/Muenchen/-/1,50-/40,00-/EURO--1100,00";

    private LinkedList<RefreshAttempt> refreshAttempts = new LinkedList<>();

    private final RestTemplate restTemplate;
    private final SearchProcessor searchProcessor;
    private final NotificationManager notificationManager;

    public SearchRefresher(RestTemplate restTemplate, SearchProcessor searchProcessor, NotificationManager notificationManager) {
        this.restTemplate = restTemplate;
        this.searchProcessor = searchProcessor;
        this.notificationManager = notificationManager;
    }

    public void refreshSearch() {
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, null, String.class);
            refreshAttempts.add(new RefreshAttempt(responseEntity.getStatusCode()));
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                searchProcessor.processSearchResults(responseEntity.getBody());
            }
        } catch (Exception ex) {
            log.error("Error on refresh", ex);
            notificationManager.sendNotification(ErrorNotification.ofException(ex));
        }
    }

    public LinkedList<RefreshAttempt> getRefreshAttempts() {
        return refreshAttempts;
    }
}
