package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.BuildPage;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.admin.CreateBuildTypePage;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static com.example.teamcity.api.generators.RandomData.getString;

public class CreateBuildTypeTest extends BaseUiTest{

    @Test(description = "User should be able to create build type from a repository URL", groups = {"Positive"})
    public void userCreatesBuildTypeFromRepo() {
        // setup
        var buildTypeName = testData.getBuildType().getName();
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());
        loginAs(testData.getUser());

        // create project
        CreateBuildTypePage.open(createdProject.getId()).createFormSuccessfully(REPO_URL).setupBuildType(buildTypeName);

        // check API
        var createdBuildType = superUserCheckRequests.<BuildType>getRequest(Endpoint.BUILD_TYPES).read("name:" + buildTypeName);
        softy.assertNotNull(createdBuildType);

        // check UI
        BuildPage.open(createdBuildType.getId()).title.shouldHave(Condition.exactText(buildTypeName));
        var foundBuildTypes = ProjectPage.open(testData.getProject().getId())
                .getBuildTypes().stream()
                .anyMatch(buildType -> buildType.getName().text().equals(buildTypeName));
        softy.assertTrue(foundBuildTypes);
    }

    @Test(description = "User should be able to create build type manually", groups = {"Positive"})
    public void userCreatesBuildTypeManually() {
        // setup
        var buildTypeName = testData.getBuildType().getName();
        var buildTypeId = testData.getBuildType().getName();
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());
        loginAs(testData.getUser());

        // create project
        CreateBuildTypePage.open(createdProject.getId()).goToCreateManually().createFormManuallySuccessfully(buildTypeName, buildTypeId);

        // check API
        var createdBuildType = superUserCheckRequests.<BuildType>getRequest(Endpoint.BUILD_TYPES).read("id:" + buildTypeId);
        softy.assertNotNull(createdBuildType);
        softy.assertEquals(createdBuildType.getName(), buildTypeName);

        // check UI
        BuildPage.open(buildTypeId).title.shouldHave(Condition.exactText(buildTypeName));
    }

    @Test(description = "User should not be able to create build with non-existent repository", groups = {"Negative"})
    public void userCreatesBuildTypeWithNonExistentRepo() {
        // setup
        var buildTypeName = testData.getBuildType().getName();
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());
        loginAs(testData.getUser());

        // create project with non-existent repo
        var errorMessage = CreateBuildTypePage.open(createdProject.getId()).createFormUnsuccessfully(REPO_URL + getString(5));
        softy.assertTrue(errorMessage.contains("No such device or address"), "Got wong error message: %s".formatted(errorMessage));

        // check that build type was not created
        var getBuildTypeResponseSC = superUserUncheckRequests.getRequest(Endpoint.BUILD_TYPES).read("name:" + buildTypeName).getStatusCode();
        softy.assertEquals(getBuildTypeResponseSC, HttpStatus.SC_NOT_FOUND);
    }

    @Test(description = "User should not be able to create build without repository", groups = {"Negative"})
    public void userCreatesBuildTypeWithoutRepo() {
        // setup
        var buildTypeName = testData.getBuildType().getName();
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());
        loginAs(testData.getUser());

        // create project with empty repo
        var errorMessage = CreateBuildTypePage.open(createdProject.getId()).createFormUnsuccessfully("");
        softy.assertEquals(errorMessage, "URL must not be empty");

        // check that build type was not created
        var getBuildTypeResponseSC = superUserUncheckRequests.getRequest(Endpoint.BUILD_TYPES).read("name:" + buildTypeName).getStatusCode();
        softy.assertEquals(getBuildTypeResponseSC, HttpStatus.SC_NOT_FOUND);
    }

}
