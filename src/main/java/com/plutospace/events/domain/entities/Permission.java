/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class Permission extends BaseEntity {

	private String name;
	private String module;
	private String description;
	private String endpoint;
	private String method;
	private String planFeature; // This is an endpoint that will trigger plan check if not null
	private Boolean isGeneral; // This is a general endpoint and must be available to all roles
}
