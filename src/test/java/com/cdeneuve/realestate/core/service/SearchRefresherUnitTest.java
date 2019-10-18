package com.cdeneuve.realestate.core.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchRefresherUnitTest {

    @InjectMocks
    private SearchRefresher searchRefresher;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SearchProcessor searchProcessor;

    @Test
    public void responseStatusCodeIsNotOk_savesAttempt_doNotCallProcessor() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("test string", HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);

        searchRefresher.refreshSearch();

        verifyZeroInteractions(searchProcessor);
        assertThat(searchRefresher.getRefreshAttempts()).hasSize(1);
    }

    @Test
    public void responseStatusCodeIsOk_savesAttempt_doNotCallProcessor() {
        String body = "test string";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);

        searchRefresher.refreshSearch();

        verify(searchProcessor, times(1)).processSearchResults(eq(body));
        assertThat(searchRefresher.getRefreshAttempts()).hasSize(1);
    }
}