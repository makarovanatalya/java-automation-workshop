package com.example.teamcity.ui;

import com.example.teamcity.api.models.Properties;
import com.example.teamcity.api.models.Property;
import com.example.teamcity.ui.pages.projectbased.BuildPage;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.example.teamcity.api.enums.Endpoint.PROJECTS;

@Test(groups = {"Regression"})
public class RunBuildTest extends BaseUiTest{
    @Test(description = "User should be able to run build", groups = {"Positive"})
    public void userRunsBuild() {
        // set step with echo "Hello, world!"
        testData.getBuildType().getSteps().getStep().get(0).setProperties(Properties.builder()
                .property(Arrays.asList(
                        Property.builder().name("script.content").value("echo \"Hello, world!\"").build(),
                        Property.builder().name("use.custom.script").value("true").build()
                )).build());

        // create project and build type
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        superUserCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        // UI steps & checks
        loginAs(testData.getUser());
        var buildTypePage = BuildPage.open(testData.getBuildType().getId()).runBuild();
        buildTypePage.getBuilds().get(0).waitForSuccess();
    }
}
