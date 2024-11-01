package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;

public class CreateBuildTypePage extends CreateBasePage {
    private static final String BUILD_SHOW_MODE = "createBuildTypeMenu";

    public static CreateBuildTypePage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_SHOW_MODE), CreateBuildTypePage.class);
    }

    public CreateBuildTypePage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public void setupBuildType(String buildTypeName) {
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
    }

}
