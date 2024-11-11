package com.example.teamcity.ui.pages.projectbased;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.elements.BuildElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class BuildPage extends ProjectBasedBasePage {
    private static final String BUILD_TYPE_URL = "/buildConfiguration/%s";

    public SelenideElement title = $("h1[class*='BuildTypePageHeader']");
    public SelenideElement runBuildButton = $("button[data-test='run-build']");
    public SelenideElement runBuildPopup = $("[class*='RunBuild__popup--']");
    private ElementsCollection buildDetailsElements = $$("[class*='BuildDetails__container']");


    public static BuildPage open(String buildTypeId) {
        return Selenide.open(BUILD_TYPE_URL.formatted(buildTypeId), BuildPage.class);
    }

    public BuildPage runBuild() {
        runBuildButton.click();
        runBuildPopup.shouldBe(Condition.partialText("is finished"), BASE_WAITING);
        return this;
    }

    public List<BuildElement> getBuilds() {
        return generatePageElements(buildDetailsElements, BuildElement::new);
    }
}
