package com.example.teamcity.api.requests.unchecked;

import com.example.teamcity.api.annotations.Searchable;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.requests.CrudInterface;
import com.example.teamcity.api.requests.Request;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UncheckedBase extends Request implements CrudInterface {

    public UncheckedBase(RequestSpecification spec, Endpoint endpoint) {
        super(spec, endpoint);
    }

    @Override
    public Response create(BaseModel model) {
        return RestAssured
                .given()
                .spec(spec)
                .body(model)
                .post(endpoint.getUrl());
    }

    @Override
    public Response read(String locator) {
        return RestAssured
                .given()
                .spec(spec)
                .get(endpoint.getUrl() + "/" + locator);
    }

    @Override
    public Response search(BaseModel model) {
        // building search URI
        StringBuilder searchURI = new StringBuilder("?locator=");
        for (var field: model.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Searchable.class)) {
                try {
                    var value = field.get(model);
                    if (value != null)
                        searchURI.append(field.getName()).append(":").append(value).append("&");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return RestAssured
                .given()
                .spec(spec)
                .get(endpoint.getUrl() + searchURI);
    }

    @Override
    public Response update(String locator, BaseModel model) {
        return RestAssured
                .given()
                .body(model)
                .spec(spec)
                .put(endpoint.getUrl() + "/" + locator);
    }

    @Override
    public Response delete(String locator) {
        return RestAssured
                .given()
                .spec(spec)
                .delete(endpoint.getUrl() + "/" + locator);
    }
}