package com.example.teamcity.ui.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.api.enums.BuildStatuses;
import lombok.Getter;

@Getter
public class BuildElement extends BasePageElement{
    private SelenideElement number;
    private SelenideElement status;

    public BuildElement(SelenideElement element) {
        super(element);
        this.number = find("[class*='Build__number']");
        this.status = find("[class*='Build__status']");
    }

    public BuildElement waitForSuccess() {
        status.shouldBe(Condition.partialText(String.valueOf(BuildStatuses.SUCCESS)), BASE_WAITING);
        return this;
    }
}
