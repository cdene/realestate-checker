package com.cdeneuve.realestate.config;

import com.cdeneuve.realestate.core.service.SearchRefresher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
public class SchedulerConfig {

    @Autowired
    private SearchRefresher searchRefresher;

    @Scheduled(fixedRate = 120000)
    public void refresh() {
        searchRefresher.refreshSearch();
    }

}
