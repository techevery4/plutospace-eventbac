/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.definitions;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class PropertyConstants {

	@Value("${api.version}")
	private String apiVersion;
}
