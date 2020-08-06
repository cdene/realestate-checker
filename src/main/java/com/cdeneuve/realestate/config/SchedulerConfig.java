package com.cdeneuve.realestate.config;

import com.cdeneuve.realestate.core.model.*;
import com.cdeneuve.realestate.core.service.SearchRefresher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@EnableScheduling
@Configuration
public class SchedulerConfig {

    @Autowired
    private SearchRefresher searchRefresher;

    private static final List<Filter> filters = List.of(
            new NumberFilter("price", BigDecimal.valueOf(0), BigDecimal.valueOf(400)),
            new NumberFilter("price", BigDecimal.valueOf(401), BigDecimal.valueOf(600)),
            new NumberFilter("price", BigDecimal.valueOf(601), BigDecimal.valueOf(800)),
            new NumberFilter("price", BigDecimal.valueOf(801), BigDecimal.valueOf(900)),
            new NumberFilter("price", BigDecimal.valueOf(901), BigDecimal.valueOf(1000)),
            new NumberFilter("price", BigDecimal.valueOf(1001), BigDecimal.valueOf(1100)),
            new NumberFilter("price", BigDecimal.valueOf(1101), BigDecimal.valueOf(1200)),
            new NumberFilter("price", BigDecimal.valueOf(1201), BigDecimal.valueOf(1300)),
            new NumberFilter("price", BigDecimal.valueOf(1301), BigDecimal.valueOf(1400)),
            new NumberFilter("price", BigDecimal.valueOf(1401), BigDecimal.valueOf(1500)),
            new NumberFilter("price", BigDecimal.valueOf(1501), BigDecimal.valueOf(1600)),
            new NumberFilter("price", BigDecimal.valueOf(1601), BigDecimal.valueOf(1700)),
            new NumberFilter("price", BigDecimal.valueOf(1701), BigDecimal.valueOf(1800)),
            new NumberFilter("price", BigDecimal.valueOf(1801), BigDecimal.valueOf(1900)),
            new NumberFilter("price", BigDecimal.valueOf(1901), BigDecimal.valueOf(2000))
    );

    @Scheduled(fixedRate = 180000)
    public void refresh() {
        filters.forEach(priceFilter -> {
                            searchRefresher.refreshSearch(
                                    Search.builder()
                                            .filter(priceFilter)
                                            .filter(new TextFilter("sorting", "2"))
                                            .build());
                            try {
                                Thread.sleep(10_000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
    }

}
