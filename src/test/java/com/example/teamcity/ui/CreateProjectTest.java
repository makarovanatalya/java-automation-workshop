package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Projects;
import com.example.teamcity.ui.pages.projectbased.ProjectPage;
import com.example.teamcity.ui.pages.projectbased.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateProjectPage;
import org.testng.annotations.Test;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {
    @Test(description = "User should be able to create project", groups = {"Positive"})
    public void userCreatesProject() {
        // preparing environments
        loginAs(testData.getUser());

        // UI steps
        CreateProjectPage.open("_Root")
                .createFormSuccessfully(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        // checking API
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).read("name:" + testData.getProject().getName());
        softy.assertNotNull(createdProject);

        // updating testData to delete project properly in teardown
        TestDataStorage.getStorage().addCreatedEntity(Endpoint.PROJECTS, createdProject);

        // checking UI
        ProjectPage.open(createdProject.getId())
                .title.shouldHave(Condition.exactText(testData.getProject().getName()));

        var foundProjects = ProjectsPage.open()
                .getProjects().stream()
                .anyMatch(project -> project.getName().text().equals(testData.getProject().getName()));

        softy.assertTrue(foundProjects);
    }

    @Test(description = "User should not be able to create project without name", groups = {"Negative"})
    public void userCreatesProjectWithoutName() {
        // preparing environments
        var projectsCountBefore = superUserUncheckRequests.getRequest(Endpoint.PROJECTS).read("").as(Projects.class).getCount();
        loginAs(testData.getUser());

        // UI steps & checks
        var errorMessage = CreateProjectPage.open("_Root").createFormUnsuccessfully("").error.text();
        softy.assertEquals(errorMessage, "URL must not be empty", "The error message is incorrect");

        // checking API
        var projectsCountAfter = superUserUncheckRequests.getRequest(Endpoint.PROJECTS).read("").as(Projects.class).getCount();
        softy.assertEquals(projectsCountBefore, projectsCountAfter, "Number of projects changed");
    }
}