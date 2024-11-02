package com.example.teamcity.ui;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Projects;
import com.example.teamcity.ui.pages.projectbased.ProjectsPage;
import org.testng.annotations.Test;

public class SearchProjectTest extends BaseUiTest{
    @Test(description = "User should be able to find a project", groups = {"Positive"})
    public void userSearchesProject() {
        //setup
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());
        loginAs(testData.getUser());

        // UI steps
        var foundedProjects = ProjectsPage.open().searchForProject(createdProject.getName()).getProjectsTreeElements();

        // API checks
        var foundedProjectsFromAPI = superUserCheckRequests.<Projects>getRequest(Endpoint.PROJECTS).search(createdProject);
        softy.assertEquals(foundedProjects.size(), foundedProjectsFromAPI.getCount().intValue(), "Number of founded projects is incorrect");
        softy.assertEquals(foundedProjects.size(), 1, "Expected to find only one project");

        // UI checks
        var firstFoundedProjects = foundedProjects.get(0);
        softy.assertEquals(firstFoundedProjects.getName().text(), createdProject.getName(), "Founded project name is incorrect");
    }
}
