package com.bengous.e2e.common.client.filter;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.filter.Filter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestAssuredFilters {

    public static List<Filter> getFilterList() {
        List<Filter> filterList = new ArrayList<>();
        // capture d'informations relatives à la requête (multipart, etc)
        filterList.add(new RequestLogFilter());
        // capture de la requête au format CURL et de la réponse
        filterList.add(new AllureRestAssured()
                               .setRequestAttachmentName("Requête")
                               .setResponseAttachmentName("Réponse"));
        return filterList;
    }
}
