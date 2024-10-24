package com.example.teamcity.api.enums;

import com.example.teamcity.api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {
    BUILD_TYPES("/app/rest/buildTypes", BuildType.class, null),
    PROJECTS("/app/rest/projects", Project.class, Projects.class),
    USERS("/app/rest/users", User.class, null),
    BUILD_QUEUE("/app/rest/buildQueue", Build.class, null),
    BUILDS("/app/rest/builds", Build.class, null);

    private final String url;
    private final Class<? extends BaseModel> modelClass;
    private final Class<? extends BaseModel> searchResultClass;
}