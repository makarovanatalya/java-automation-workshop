package com.example.teamcity.api;

import com.example.teamcity.api.enums.RoleTypes;
import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.BuildStatuses.SUCCESS;
import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        var createdProject = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        testData.getUser().setRoles(generate(Roles.class, RoleTypes.PROJECT_ADMIN, "p:" + createdProject.getId()));
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).create(testData.getBuildType());

        softy.assertEquals(createdProject.getId(), createdBuildType.getProject().getId(), "Created build type is associated with incorrect project");
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        var testData2 = generate();

        // prepare both projects
        for (TestData tData:  Arrays.asList(testData, testData2)) {
            var createdProject = superUserCheckRequests.<Project>getRequest(PROJECTS).create(tData.getProject());
            tData.getUser().setRoles(generate(Roles.class, RoleTypes.PROJECT_ADMIN, "p:" + createdProject.getId()));
            superUserCheckRequests.getRequest(USERS).create(tData.getUser());
        }

        // try to create build by user1 in project2
        testData.getBuildType().setProject(testData2.getProject());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("You do not have enough permissions to edit project with id: %s".formatted(testData2.getProject().getId())));
    }

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
            createdBuild = superUserCheckRequests.<Build>getRequest(BUILDS).read(createdBuild.getId());
            if (createdBuild.getStatus() != null && createdBuild.getStatus().equals(SUCCESS))
                break;
            Thread.sleep(1000);
        }
        softy.assertEquals(createdBuild.getStatus(), SUCCESS, "Incorrect build status %s".formatted(createdBuild.getStatus()));
    }
}