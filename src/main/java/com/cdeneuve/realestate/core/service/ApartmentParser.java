package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.exception.ApartmentDetailsParsingException;
import com.cdeneuve.realestate.core.model.Apartment;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApartmentParser {
    private static final String TITLE_NODE_CLASS = "result-list-entry__brand-title-container";
    public static final String ADDRESS_ELEMENT_CLASS = "result-list-entry__address";
    public static final String APARTMENT_RESULT_CLASS = "result-list__listing";

    public List<Apartment> parseApartmentIdsFromHtml(String htmlContent) {
        Document document = Jsoup.parse(htmlContent, "UTF-8");
        return Optional.ofNullable(document.getElementById("resultListItems"))
                .map(this::parseResultListElement)
                .orElse(new ArrayList<>());
    }

    private List<Apartment> parseResultListElement(Element element) {
        List<Apartment> result = element.getElementsByClass(APARTMENT_RESULT_CLASS).stream()
                .map(this::parseApartment)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        Collections.reverse(result);
        return result;
    }

    Optional<Apartment> parseApartment(Node apartmentNode) {
        String apartmentId = apartmentNode.attr("data-id");
        try {
            log.info("Process apartment with id={}", apartmentId);
            Element apartmentElement = (Element) apartmentNode;
            Elements dataElements = apartmentElement.getElementsByClass("result-list-entry__data");
            if (dataElements.isEmpty()) {
                log.info("Skipping an apartment with id={} because data is empty:\n{}", apartmentId, apartmentElement);
                return Optional.empty();
            } else {
                return parseApartmentDetails(apartmentId, apartmentElement);
            }
        } catch (Exception ex) {
            log.error("Error during a parsing process: {}", apartmentId, ex);
            return Optional.empty();
        }
    }

    private Optional<Apartment> parseApartmentDetails(String apartmentId, Element apartmentElement) throws ApartmentDetailsParsingException {
        Element apartmentDataElement = apartmentElement.getElementsByClass("result-list-entry__data").get(0);
        String title = parseTitle(apartmentDataElement);
        String address = parseAddress(apartmentDataElement);
        Details details = parseDetails(apartmentDataElement);

        return Optional.of(
                Apartment.builder()
                        .id(apartmentId)
                        .title(title)
                        .address(address)
                        .price(details.getPrice())
                        .area(details.getArea())
                        .rooms(details.getRooms())
                        .tags(details.getTags())
                        .timestamp(LocalDateTime.now(ZoneId.of("Europe/Berlin")))
                        .build());
    }

    private String parseTitle(Element apartmentData) throws ApartmentDetailsParsingException {
        try {
            Element titleElement = apartmentData.getElementsByClass(TITLE_NODE_CLASS).get(0);
            return titleElement.text().replace("NEU", "");
        } catch (Exception e) {
            throw new ApartmentDetailsParsingException("title", TITLE_NODE_CLASS, apartmentData, e);
        }
    }

    private String parseAddress(Element apartmentData) throws ApartmentDetailsParsingException {
        try {
            Element addressElement = apartmentData.getElementsByClass(ADDRESS_ELEMENT_CLASS).get(0);
            return addressElement.text();
        } catch (Exception e) {
            throw new ApartmentDetailsParsingException("address", ADDRESS_ELEMENT_CLASS, apartmentData, e);
        }
    }

    private Details parseDetails(Element apartmentData) {
        Element detailsElement = apartmentData.getElementsByClass("result-list-entry__criteria").get(0);
        Elements primary = detailsElement.getElementsByClass("grid-item result-list-entry__primary-criterion ");

        BigDecimal price = BigDecimal.ZERO;
        BigDecimal area = BigDecimal.ZERO;
        BigDecimal numberOfRooms = BigDecimal.ZERO;


        for (int i = 0; i < Math.min(primary.size(), 3); i++) {
            Element element = primary.get(i);
            String string = element.getElementsByTag("dd").text();
            if (string.contains("€")) {
                price = new BigDecimal(string.replace("€", "")
                                               .replace(".", "")
                                               .replace(",", ".")
                                               .trim());
            } else if (string.contains("m²")) {
                area = new BigDecimal(string.replace("m²", "")
                                              .replace(",", ".")
                                              .trim());
            } else {
                String numberOfRoomsString = element.getElementsByTag("dd").get(0).getElementsByClass("onlyLarge")
                        .text()
                        .replace(",", ".")
                        .trim();
                numberOfRooms = new BigDecimal(numberOfRoomsString);
            }
        }

        Elements tagsElement = detailsElement.getElementsByClass("result-list-entry__secondary-criteria");
        List<String> tags = tagsElement == null || tagsElement.isEmpty() ?
                new ArrayList<>()
                : tagsElement.get(0).getElementsByTag("li").stream()
                        .map(Element::text)
                        .collect(Collectors.toList());

        return Details.builder()
                .price(price)
                .area(area)
                .rooms(numberOfRooms)
                .tags(tags)
                .build();
    }

    @Builder
    @Getter
    @ToString
    private static class Details {
        private final BigDecimal price;
        private final BigDecimal area;
        private final BigDecimal rooms;
        private final List<String> tags;
    }

}
