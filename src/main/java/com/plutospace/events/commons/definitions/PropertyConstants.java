/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.definitions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class PropertyConstants {

	@Value("${api.version}")
	private String apiVersion;
}
