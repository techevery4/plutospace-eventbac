/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import com.plutospace.events.domain.entities.Plan;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanRequest {

	private String type;
	private String name;
	private Plan.Features features;
	private Double priceNaira;
	private Double priceUsd;
}
