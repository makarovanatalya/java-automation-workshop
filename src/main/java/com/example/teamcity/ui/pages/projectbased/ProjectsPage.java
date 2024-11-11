package com.example.teamcity.ui.pages.projectbased;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.elements.ProjectElement;
import com.example.teamcity.ui.elements.ProjectsTreeElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProjectsPage extends ProjectBasedBasePage {
    private static final String PROJECTS_URL = "/favorite/projects";

    private ElementsCollection projectElements = $$("div[class*='Subproject__container']");
    private SelenideElement spanFavoriteProjects = $("span[class='ProjectPageHeader__title--ih']");

    private SelenideElement header = $(".MainPanel__router--gF > div");
    private ElementsCollection projectsTreeElements = $$("div[class*='ProjectsTreeItem__row']");

    private ElementsCollection lalala = $$("div[data-test-itemtype]");

    public ProjectsPage() {
        header.shouldBe(Condition.visible, BASE_WAITING);
    }

    public static ProjectsPage open() {
        return Selenide.open(PROJECTS_URL, ProjectsPage.class);
    }

    public ProjectsPage searchForProject(String projectName) {
        searchForEntity(projectName);
        return this;
    }

    public List<ProjectElement> getProjects() {
        return generatePageElements(projectElements, ProjectElement::new);
    }

    public List<ProjectsTreeElement> getProjectsTreeElements() {
        return generatePageElements(projectsTreeElements, ProjectsTreeElement::new);
    }

}
