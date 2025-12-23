/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.integrations;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.domain.data.request.GoMailerRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MiddlewareService {

	private final GoMailerService goMailerService;
	private final PropertyConstants propertyConstants;

	@Async
	public void sendInvite(GoMailerRequest goMailerRequest) {
		log.info("Sending Invite");
		goMailerRequest.setBcc(propertyConstants.getGoMailerSenderEmail());
		goMailerRequest.setTemplate_code(propertyConstants.getGoMailerUserInviteTemplateCode());

		goMailerService.sendEmail(goMailerRequest);
		log.info("After Sending Invite");
	}
}
