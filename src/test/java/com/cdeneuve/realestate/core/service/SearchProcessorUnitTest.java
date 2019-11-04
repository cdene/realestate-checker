package com.cdeneuve.realestate.core.service;


import com.cdeneuve.realestate.core.notification.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class SearchProcessorUnitTest {

    @InjectMocks
    private SearchProcessor searchProcessor;

    @Mock
    private ApartmentParser apartmentParser;

    @Mock
    private NotificationService notificationService;

    @Test
    public void test() {
        String response = getFileContent("test_response.html");
        System.out.println();
//        ResponseEntity<String> responseEntity = new ResponseEntity<>()
//        when(restTemplate.exchange(any(), any())).thenReturn()
    }

    private String getFileContent(String name) {
        String result = "";
        try {
            URL file = this.getClass().getResource("/" + name);
            List<String> content = Files.readAllLines(Paths.get(file.getPath()));
            return content.stream().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}