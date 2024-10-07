package com.example.teamcity.api.models;
import com.example.teamcity.api.annotations.Parameterizable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties extends BaseModel {
//    private Integer count = 3;
    @Parameterizable
    private List<Property> property;
}
