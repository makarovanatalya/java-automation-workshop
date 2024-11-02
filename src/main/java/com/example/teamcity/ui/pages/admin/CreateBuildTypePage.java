package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildTypePage extends CreateBasePage {
    private static final String BUILD_SHOW_MODE = "createBuildTypeMenu";
    private static final String FROM_URL_ANCHOR = "#createFromUrl";
    private static final String MANUALLY_ANCHOR = "#createManually";

    SelenideElement createManuallyLink = $("a[href='%s']".formatted(MANUALLY_ANCHOR));
    SelenideElement unprocessedBuildTypeCreatedConfirmation = $("#unprocessed_buildTypeCreated");

    public static CreateBuildTypePage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_SHOW_MODE) + FROM_URL_ANCHOR, CreateBuildTypePage.class);
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

    public CreateBuildTypePage goToCreateManually () {
        createManuallyLink.click();
        buildTypeNameInput.shouldBe(Condition.visible, BASE_WAITING);
        buildTypeIdInput.shouldBe(Condition.visible, BASE_WAITING);
        return this;
    }

    public void createFormManuallySuccessfully(String buildTypeName, String buildTypeId) {
        buildTypeNameInput.val(buildTypeName);
        buildTypeIdInput.val(buildTypeId);
        createButton.click();
        unprocessedBuildTypeCreatedConfirmation.shouldBe(Condition.visible, BASE_WAITING);
    }

    public void setupBuildType(String buildTypeName) {
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
    }

}
