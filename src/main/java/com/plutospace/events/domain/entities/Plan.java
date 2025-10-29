/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;
import com.plutospace.events.domain.data.PlanType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class Plan extends BaseEntity {

	private PlanType type;
	private String name;
	private List<String> features = new ArrayList<>();
	private Double priceNaira;
	private Double priceUsd;
}
