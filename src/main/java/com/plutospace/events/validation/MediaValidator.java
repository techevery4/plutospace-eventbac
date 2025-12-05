/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.UploadMediaRequest;

@Component
public class MediaValidator {

	public void validate(UploadMediaRequest uploadMediaRequest) {
		String typeCannotBeNullValidationMessage = "Type cannot be empty";
		String accountIdCannotBeNullValidationMessage = "Media must be tied to an account";
		String sizeCannotBeNullValidationMessage = "Size cannot be empty";

		if (StringUtils.isBlank(uploadMediaRequest.getType())) {
			throw new GeneralPlatformDomainRuleException(typeCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(uploadMediaRequest.getAccountId())) {
			throw new GeneralPlatformDomainRuleException(accountIdCannotBeNullValidationMessage);
		}
		if (ObjectUtils.isEmpty(uploadMediaRequest.getSize()) || uploadMediaRequest.getSize() <= 0) {
			throw new GeneralPlatformDomainRuleException(sizeCannotBeNullValidationMessage);
		}
	}
}
