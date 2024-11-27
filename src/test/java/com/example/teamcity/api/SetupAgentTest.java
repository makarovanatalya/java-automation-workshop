package com.example.teamcity.api;

import com.example.teamcity.api.models.AuthorizedInfo;
import com.example.teamcity.api.requests.ManageAgentsRequest;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.Test;

public class SetupAgentTest extends BaseApiTest {
    private final ManageAgentsRequest manageAgentsRequest = new ManageAgentsRequest(Specifications.superUserSpec());

    @Test(groups = {"Setup"})
    public void setupTeamCityAgentTest() throws InterruptedException {
        var agents = manageAgentsRequest.get("?locator=authorized:any");
        for (int i = 0; i < 60; i++) {
            if (agents.getCount() > 0)
                break;
            Thread.sleep(10000);
            agents = manageAgentsRequest.get("?locator=authorized:any");
        }
        softy.assertTrue(agents.getCount() > 0, "Not found an agent to set up");
        var agentId = agents.getAgent().get(0).getId();
        manageAgentsRequest.update("id:" + agentId + "/authorizedInfo", new AuthorizedInfo());
    }
}
