package com.example.teamcity.api.models;

import com.example.teamcity.api.enums.BuildStatuses;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Build extends BaseModel {
    private String id;
    private BuildStatuses status;
}
