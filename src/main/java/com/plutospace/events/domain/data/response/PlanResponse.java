/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanResponse {

	private String id;
	private String type;
	private String name;
	private List<String> features;
	private Double priceNaira;
	private Double priceUsd;
}
