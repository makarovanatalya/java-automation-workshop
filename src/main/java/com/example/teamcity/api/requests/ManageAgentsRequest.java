package com.example.teamcity.api.requests;

import com.example.teamcity.api.models.Agents;
import com.example.teamcity.api.models.AuthorizedInfo;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

public class ManageAgentsRequest {
    private static final String MANAGE_AGENTS_URL = "/app/rest/agents";
    private static final String UPDATE_AGENT_URL = MANAGE_AGENTS_URL + "/%s";
    private RequestSpecification spec;

    public ManageAgentsRequest(RequestSpecification spec) {
        this.spec = spec;
    }

    public Agents get(String locator) {
        return RestAssured.given()
                .spec(spec)
                .get(MANAGE_AGENTS_URL + locator)
                .then().assertThat().statusCode(HttpStatus.SC_OK).
                extract().as(Agents.class);
    }

    public void update(String locator, AuthorizedInfo authorizedInfo) {
        RestAssured.given()
                .spec(spec)
                .body(authorizedInfo)
                .put(UPDATE_AGENT_URL.formatted(locator))
                .then().assertThat().statusCode(HttpStatus.SC_OK);
    }
}