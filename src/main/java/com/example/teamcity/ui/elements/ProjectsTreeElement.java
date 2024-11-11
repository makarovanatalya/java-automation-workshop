package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class ProjectsTreeElement extends  BasePageElement{
    private SelenideElement name;

    public ProjectsTreeElement(SelenideElement element) {
        super(element);
        this.name = find("span[class*='ProjectsTreeItem__name']");
    }
}
