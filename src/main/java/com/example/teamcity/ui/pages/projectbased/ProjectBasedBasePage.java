package com.example.teamcity.ui.pages.projectbased;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.pages.BasePage;

import static com.codeborne.selenide.Selenide.$;

public class ProjectBasedBasePage extends BasePage {
    protected SelenideElement searchInput = $("#search-projects");
    protected SelenideElement clearSearchButton = $("button[class*='ring-input-clear']");
    private SelenideElement projectsTreeHeader = $("[class*='ProjectsTreeItem__title']");

    protected void searchForEntity(String text) {
        searchInput.val(text);
        clearSearchButton.shouldBe(Condition.visible, BASE_WAITING);
        projectsTreeHeader.shouldBe(Condition.not(Condition.visible), BASE_WAITING);
    }

}
