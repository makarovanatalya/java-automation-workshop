package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.BuildPage;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.admin.CreateBuildTypePage;
import org.testng.annotations.Test;

public class CreateBuildTest extends BaseUiTest{

    @Test(description = "User should be able to create build", groups = {"Positive"})
    public void userCreatesBuild() {
        // setup
        var buildName = testData.getBuildType().getName();
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());
        loginAs(testData.getUser());

        // create project
        CreateBuildTypePage.open(createdProject.getId()).createForm(REPO_URL).setupBuildType(buildName);

        // check API
        var createdBuildType = superUserCheckRequests.<BuildType>getRequest(Endpoint.BUILD_TYPES).read("name:" + buildName);
        softy.assertNotNull(createdBuildType);

        // check UI
        BuildPage.open(createdBuildType.getId()).title.shouldHave(Condition.exactText(buildName));
        var foundBuildTypes = ProjectPage.open(testData.getProject().getId())
                .getBuildTypes().stream()
                .anyMatch(buildType -> buildType.getName().text().equals(buildName));
        softy.assertTrue(foundBuildTypes);
    }

}
