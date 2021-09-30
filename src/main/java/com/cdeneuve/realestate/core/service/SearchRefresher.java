package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.model.ErrorNotification;
import com.cdeneuve.realestate.core.model.Filter;
import com.cdeneuve.realestate.core.model.Search;
import com.cdeneuve.realestate.core.notification.NotificationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchRefresher {
    private static final String searchUrl = "https://www.immobilienscout24.de/Suche/de/bayern/muenchen/wohnung-mieten?";

    private final RestTemplate restTemplate;
    private final SearchProcessor searchProcessor;
    private final NotificationManager notificationManager;

    public void refreshSearch(Search search) {
        try {
            String filterStr = search.getFilters().stream()
                    .map(Filter::getValue)
                    .collect(Collectors.joining("&"));

            String response = restTemplate.postForObject(searchUrl + filterStr, null, String.class);
            searchProcessor.processSearchResults(response);
        } catch (Exception ex) {
            log.error("Error on refresh", ex);
            notificationManager.sendNotification(ErrorNotification.ofException(ex));
        }
    }
}
