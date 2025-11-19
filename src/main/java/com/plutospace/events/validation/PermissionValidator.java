/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.SavePermissionRequest;

@Component
public class PermissionValidator {

	public void validate(SavePermissionRequest savePermissionRequest) {
		String nameCannotBeNullValidationMessage = "Name cannot be empty";
		String methodCannotBeNullValidationMessage = "Method cannot be empty";
		String endpointCannotBeNullValidationMessage = "Endpoint cannot be empty";
		String moduleCannotBeNullValidationMessage = "Module cannot be empty";

		if (StringUtils.isBlank(savePermissionRequest.name())) {
			throw new GeneralPlatformDomainRuleException(nameCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(savePermissionRequest.method())) {
			throw new GeneralPlatformDomainRuleException(methodCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(savePermissionRequest.endpoint())) {
			throw new GeneralPlatformDomainRuleException(endpointCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(savePermissionRequest.module())) {
			throw new GeneralPlatformDomainRuleException(moduleCannotBeNullValidationMessage);
		}
	}
}
