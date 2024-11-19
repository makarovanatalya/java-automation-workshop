package com.example.teamcity.api;

import com.example.teamcity.api.enums.RoleTypes;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Projects;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static com.example.teamcity.api.enums.Endpoint.USERS;
import static com.example.teamcity.api.generators.RandomData.getRandomSpecialCharacter;
import static com.example.teamcity.api.generators.RandomData.getString;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class ProjectTest  extends BaseApiTest {
    @Test(description = "Project can not be created with empty name", groups = {"Negative", "CRUD"})
    public void projectCanNotBeCreatedWithEmptyName() {
        testData.getProject().setName("");
        superUserUncheckRequests.getRequest(PROJECTS).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Project name cannot be empty"));
    }

    @Test(description = "Project can not be created with empty id", groups = {"Negative", "CRUD"})
    public void projectCanNotBeCreatedWithEmptyId() {
        testData.getProject().setId("");
        superUserUncheckRequests.getRequest(PROJECTS).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(Matchers.containsString("Project ID must not be empty."));
    }

    @Test(description = "Project can not be created with non-letter character in id", groups = {"Negative", "CRUD"})
    public void projectCanNotBeCreatedWithSpecialCharacterInId() {
        String specialCharacter = getRandomSpecialCharacter();
        testData.getProject().setId(testData.getProject().getId() + specialCharacter);
        superUserUncheckRequests.getRequest(PROJECTS).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(Matchers.containsString("Project ID \"%s\" is invalid: contains unsupported character '%s'".formatted(testData.getProject().getId(), specialCharacter)));
    }

    // the same test for name is needed, but now there's no restriction from API side (only from UI, which is no good)
    @Test(description = "Project id can not contain more than 225 characters", groups = {"Negative", "CRUD"})
    public void projectIdCanNotContainMoreThan225Characters() {
        testData.getProject().setId(getString(226));
        superUserUncheckRequests.getRequest(PROJECTS).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(Matchers.containsString("Project ID \"%s\" is invalid: it is 226 characters long while the maximum length is 225".formatted(testData.getProject().getId())));
    }

    @Test(description = "Project can not be created with non existent parent", groups = {"Negative", "CRUD"})
    public void projectCanNotBeCreatedWithNonExistentParent() {
        var nonExistentProject = generate().getProject();
        testData.getProject().setParentProject(nonExistentProject);
        superUserUncheckRequests.getRequest(PROJECTS).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString("Project cannot be found by external id '%s'".formatted(nonExistentProject.getId())));
    }

    @Test(description = "Project can not be created with duplicated id", groups = {"Negative", "CRUD"})
    public void projectCanNotBeCreatedWithDuplicatedId() {
        var projectWithDuplicatedId = generate().getProject();
        projectWithDuplicatedId.setId(testData.getProject().getId());
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        superUserUncheckRequests.getRequest(PROJECTS).create(projectWithDuplicatedId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Project ID \"%s\" is already used by another project".formatted(projectWithDuplicatedId.getId())));
    }

    @Test(description = "Project can not be created with duplicated name", groups = {"Negative", "CRUD"})
    public void projectCanNotBeCreatedWithDuplicatedName() {
        var projectWithDuplicatedName = generate().getProject();
        projectWithDuplicatedName.setName(testData.getProject().getName());
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        superUserUncheckRequests.getRequest(PROJECTS).create(projectWithDuplicatedName)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Project with this name already exists: %s".formatted(projectWithDuplicatedName.getName())));
    }

    @Test(description = "Project can be created", groups = {"Positive", "CRUD"})
    public void projectCanBeCreated() {
        var requestedProject = testData.getProject();
        var createdProject = superUserCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        softy.assertEquals(createdProject.getId(), requestedProject.getId(), "Project id is not correct");
        softy.assertEquals(createdProject.getName(), requestedProject.getName(), "Project name is not correct");
        softy.assertEquals(createdProject.getParentProject().getId(), "_Root", "Created project should be a root project");
    }

    @Test(description = "Project can be created as a subproject", groups = {"Positive", "CRUD"})
    public void subProjectCanBeCreated() {
        var parentProject = testData.getProject();
        var subProject = generate().getProject();
        subProject.setParentProject(parentProject);

        var createdParentProject = superUserCheckRequests.<Project>getRequest(PROJECTS).create(parentProject);
        var createdSubProject = superUserCheckRequests.<Project>getRequest(PROJECTS).create(subProject);
        softy.assertEquals(createdSubProject.getParentProject().getId(), createdParentProject.getId(), "Created project has an incorrect parent project id");
        softy.assertEquals(createdSubProject.getParentProject().getName(), createdParentProject.getName(), "Created project has an incorrect parent project name");
    }

    @Test(description = "Project can be copied", groups = {"Positive", "CRUD"})
    public void projectCanBeCopied() {
        // prepare test data
        var project = testData.getProject();
        var copiedProject = generate().getProject();
        copiedProject.setSourceProject(project);

        // create both projects
        var createdProject = superUserCheckRequests.<Project>getRequest(PROJECTS).create(project);
        var createdCopiedProject = superUserCheckRequests.<Project>getRequest(PROJECTS).create(copiedProject);

        // check that they have the same root (after implementing properties for project needs to add checks)
        softy.assertEquals(createdProject.getParentProject().getId(), createdCopiedProject.getParentProject().getId(), "Copied project has different parent project");
    }

    @Test(description = "Project admin can't create Root project", groups = {"Negative", "Roles"})
    public void projectAdminCanNotCreateRootProject() {
        // create project and user with role project admin for this project
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        testData.getUser().setRoles(generate(Roles.class, RoleTypes.PROJECT_ADMIN, "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        // try to create root project by user
        var rootProject = generate().getProject();
        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(rootProject)
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("You do not have \"Create subproject\" permission in project with internal id: _Root"));
    }

    @Test(description = "Project admin can create subproject in their project", groups = {"Positive", "Roles"})
    public void projectAdminCanCreateSubproject() {
        var parentProject = testData.getProject();
        var subProject = generate().getProject();
        subProject.setParentProject(parentProject);

        superUserCheckRequests.getRequest(PROJECTS).create(parentProject);
        testData.getUser().setRoles(generate(Roles.class, RoleTypes.PROJECT_ADMIN, "p:" + parentProject.getId()));
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).create(subProject);
        softy.assertEquals(subProject.getParentProject().getId(), createdProject.getParentProject().getId(), "Parent project is incorrect");
    }

    @Test(description = "System admin can create Root project", groups = {"Positive", "Roles"})
    public void systemAdminCanCreateRootProject() {
        var project = testData.getProject();
        testData.getUser().setRoles(generate(Roles.class, RoleTypes.SYSTEM_ADMIN));
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).create(project);
        softy.assertEquals("_Root", createdProject.getParentProject().getId(), "Parent project is incorrect");
    }

    @Test(description = "Project can be found by name", groups = {"Positive", "Search"})
    public void projectCanBeFoundByName() {
        var project = testData.getProject();
        superUserCheckRequests.<Project>getRequest(PROJECTS).create(project);

        var foundedProjects = superUserCheckRequests.<Projects>getRequest(PROJECTS).search(project);
        var foundedProject = foundedProjects.getProject().get(0);

        softy.assertTrue(foundedProjects.getCount().equals(1), "Unexpected count of founded projects");
        softy.assertTrue(foundedProjects.getProject().size() == 1, "Unexpected count of founded projects");
        softy.assertEquals(project.getId(), foundedProject.getId(), "Id of founded project is incorrect");
        softy.assertEquals(project.getName(), foundedProject.getName(), "Name of founded project is incorrect");
    }
}
