package com.example.teamcity.api;

import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.models.Properties;
import com.example.teamcity.api.models.Property;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.BuildStatuses.SUCCESS;
import static com.example.teamcity.api.enums.Endpoint.*;

@Test(groups = {"Regression"})
public class BuildTest extends BaseApiTest {
    @Test(description = "Build runs successfully", groups = {"Positive", "CRUD"})
    public void buildRunsSuccessfully() throws InterruptedException {
        // set step with echo "Hello, world!"
        testData.getBuildType().getSteps().getStep().get(0).setProperties(Properties.builder()
                .property(Arrays.asList(
                        Property.builder().name("script.content").value("echo \"Hello, world!\"").build(),
                        Property.builder().name("use.custom.script").value("true").build()
                )).build());

        // create project and build type
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        superUserCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        // run build
        var createdBuild = superUserCheckRequests.<Build>getRequest(BUILD_QUEUE).create(testData.getBuildQueue());

        // wait for build finished
        for (int i = 0; i < 10; i++) {
            createdBuild = superUserCheckRequests.<Build>getRequest(BUILDS).read("id:" + createdBuild.getId());
            if (createdBuild.getStatus() != null && createdBuild.getStatus().equals(SUCCESS))
                break;
            Thread.sleep(1000);
        }
        softy.assertEquals(createdBuild.getStatus(), SUCCESS, "Incorrect build status %s".formatted(createdBuild.getStatus()));
    }
}
