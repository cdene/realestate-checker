package com.cdeneuve.realestate.core.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.ToString;

@ToString
@Builder(access = AccessLevel.PRIVATE)
public class ErrorNotification implements Notification {
    private final String title;
    private final String payload;

    public static ErrorNotification ofException(Exception ex) {
        return ErrorNotification.builder()
                .title("Error notification")
                .payload(ex.getClass().getSimpleName() + " " + ex.getMessage())
                .build();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getPayload() {
        return payload;
    }
}
