package com.cdeneuve.realestate.core.service;


import com.cdeneuve.realestate.core.model.Apartment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ApartmentParserUnitTest {

    @InjectMocks
    private ApartmentParser apartmentParser;

    @Test
    public void test() {
        String htmlPage = getFileContent("test_response.html");
        List<Apartment> apartments = apartmentParser.parseApartmentIdsFromHtml(htmlPage);

        assertThat(apartments).isNotEmpty();
    }

    @Test
    public void parseSingleApartment() {
        String htmlContent = getFileContent("apartment_node_example.html");
        Document document = Jsoup.parse(htmlContent, "UTF-8");
        Element body = document.getElementsByTag("body").get(0);
        Node apartmentNode = body.childNodes().get(0);
        Apartment apartment = apartmentParser.parseApartment(apartmentNode)
                .orElseThrow(() -> new AssertionError("No apartment present"));

        assertThat(apartment.getId()).isNotBlank();
        assertThat(apartment.getAddress()).isNotBlank();
        assertThat(apartment.getPrice()).isGreaterThan(BigDecimal.ZERO);
        assertThat(apartment.getRooms()).isGreaterThan(BigDecimal.ZERO);
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