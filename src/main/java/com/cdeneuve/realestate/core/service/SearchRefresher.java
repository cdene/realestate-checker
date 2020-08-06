package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.model.*;
import com.cdeneuve.realestate.core.notification.NotificationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;

@Slf4j
@Service
public class SearchRefresher {
    private static final String searchUrl = "https://www.immobilienscout24.de/Suche/de/bayern/muenchen/wohnung-mieten?numberofrooms=1.5-&price=-1100.0&livingspace=40.0-&sorting=2";

    private final LinkedList<RefreshAttempt> refreshAttempts = new LinkedList<>();

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
            String response = restTemplate.postForObject(searchUrl, null, String.class);
            searchProcessor.processSearchResults(response);
        } catch (Exception ex) {
            log.error("Error on refresh", ex);
            notificationManager.sendNotification(ErrorNotification.ofException(ex));
        }
    }

    public LinkedList<RefreshAttempt> getRefreshAttempts() {
        return refreshAttempts;
    }
}
