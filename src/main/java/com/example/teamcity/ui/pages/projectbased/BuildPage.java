package com.example.teamcity.ui.pages.projectbased;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class BuildPage extends ProjectBasedBasePage {
    private static final String BUILD_TYPE_URL = "/buildConfiguration/%s";

    public SelenideElement title = $("h1[class*='BuildTypePageHeader']");

    public static BuildPage open(String buildTypeId) {
        return Selenide.open(BUILD_TYPE_URL.formatted(buildTypeId), BuildPage.class);
    }
}
