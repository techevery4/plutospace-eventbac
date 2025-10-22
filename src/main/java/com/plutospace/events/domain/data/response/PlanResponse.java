package com.plutospace.events.domain.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanResponse {

    private String id;
    private String type;
    private List<String> features = new ArrayList<>();
    private Double priceNaira;
    private Double priceUsd;
}
