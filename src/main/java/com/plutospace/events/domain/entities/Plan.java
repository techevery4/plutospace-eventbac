package com.plutospace.events.domain.entities;

import com.plutospace.events.commons.entities.BaseEntity;
import com.plutospace.events.domain.data.PlanType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class Plan extends BaseEntity {

    private PlanType type;
    private List<String> features = new ArrayList<>();
    private Double priceNaira;
    private Double priceUsd;
}
