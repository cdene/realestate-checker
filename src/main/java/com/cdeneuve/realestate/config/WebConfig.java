package com.cdeneuve.realestate.config;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@Configuration
public class WebConfig {

    @Bean
    public RestTemplate restTemplate() {
        List<ClientHttpRequestInterceptor> interceptors = List.of(
                new HeaderRequestInterceptor("Content-Type", "application/json;charset=UTF-8"),
                new HeaderRequestInterceptor("Accept", "*/*"),
                new HeaderRequestInterceptor("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")
        );
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S"))
                .setTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/Berlin")))
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
