package com.cdeneuve.realestate.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;

import java.io.IOException;

public class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {
    private final String headerName;
    private final String headerValue;

    public HeaderRequestInterceptor(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        httpRequest.getHeaders().set(headerName, headerValue);
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
