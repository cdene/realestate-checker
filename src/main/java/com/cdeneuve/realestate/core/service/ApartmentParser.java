package com.cdeneuve.realestate.core.service;

import com.cdeneuve.realestate.core.model.Apartment;
import com.cdeneuve.realestate.core.model.ErrorNotification;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApartmentParser {
    private final NotificationService notificationService;

    public ApartmentParser(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public List<Apartment> parseApartmentIdsFromHtml(String htmlContent) {
        Document document = Jsoup.parse(htmlContent, "UTF-8");
        return Optional.ofNullable(document.getElementById("resultListItems"))
                .map(this::parseResultListElement)
                .orElse(new ArrayList<>());
    }

    private List<Apartment> parseResultListElement(Element element) {
        List<Apartment> result = element.childNodes().stream()
                .map(this::parseApartment)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        Collections.reverse(result);
        return result;
    }

    Optional<Apartment> parseApartment(Node apartmentNode) {
        try {
            String apartmentId = apartmentNode.attr("data-id");

            Element apartmentElement = (Element) apartmentNode;

            Elements dataElements = apartmentElement.getElementsByClass("result-list-entry__data");
            if (dataElements.isEmpty()) {
                return Optional.empty();
            } else {
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
                                .timestamp(LocalDateTime.now())
                                .build());
            }
        } catch (Exception ex) {
            notificationService.sendNotification(ErrorNotification.ofException(ex));
            return Optional.empty();
        }
    }

    private String parseTitle(Element apartmentData) {
        Element titleElement = apartmentData.getElementsByClass("result-list-entry__brand-title-container").get(0);
        return titleElement.text().replace("NEU", "");
    }

    private String parseAddress(Element apartmentData) {
        Element addressElement = apartmentData.getElementsByClass("result-list-entry__address").get(0);
        return addressElement.text();
    }

    private Details parseDetails(Element apartmentData) {
        Element detailsElement = apartmentData.getElementsByClass("result-list-entry__criteria margin-bottom-s").get(0);
        Elements primary = detailsElement.getElementsByClass("grid-item result-list-entry__primary-criterion ");
        String priceString = primary.get(0).getElementsByTag("dd").text()
                .replace("€", "")
                .replace(".", "")
                .replace(",", ".")
                .trim();
        String areaString = primary.get(1).getElementsByTag("dd").text()
                .replace("m²", "")
                .replace(",", ".")
                .trim();
        String numberOfRoomsString = primary.get(2).getElementsByTag("dd").get(0).getElementsByClass("onlyLarge").text()
                .replace(",", ".")
                .trim();

        Elements tagsElement = detailsElement.getElementsByClass("result-list-entry__secondary-criteria");
        List<String> tags = tagsElement == null || tagsElement.isEmpty() ?
                new ArrayList<>()
                : tagsElement.get(0).getElementsByTag("li").stream()
                .map(Element::text)
                .collect(Collectors.toList());

        return Details.builder()
                .price(new BigDecimal(priceString))
                .area(new BigDecimal(areaString))
                .rooms(new BigDecimal(numberOfRoomsString))
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
