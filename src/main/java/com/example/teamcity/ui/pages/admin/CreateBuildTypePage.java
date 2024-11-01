package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

public class CreateBuildTypePage extends CreateBasePage {
    private static final String BUILD_SHOW_MODE = "createBuildTypeMenu";

    public static CreateBuildTypePage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_SHOW_MODE), CreateBuildTypePage.class);
    }

    public String createFormUnsuccessfully(String url) {
        uncheckedBaseCreateForm(url);
        error.shouldBe(Condition.visible, BASE_WAITING);
        return error.text();
    }

    public CreateBuildTypePage createFormSuccessfully(String url) {
        baseCreateForm(url);
        return this;
    }

    public void setupBuildType(String buildTypeName) {
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
    }

}
