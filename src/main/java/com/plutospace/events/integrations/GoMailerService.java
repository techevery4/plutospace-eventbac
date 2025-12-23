/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.integrations;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.plutospace.events.commons.config.restclient.GoMailerRestClient;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.domain.data.request.GoMailerRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoMailerService {

	private final PropertyConstants propertyConstants;
	private final GoMailerRestClient goMailerRestClient;

	public void sendEmail(GoMailerRequest goMailerRequest) {
		String url = propertyConstants.getGoMailerBaseUrl() + propertyConstants.getGoMailerTransactionalUrl();
		ParameterizedTypeReference<Void> typeRef = new ParameterizedTypeReference<>() {
		};
		goMailerRestClient.postV2(url, goMailerRequest, typeRef);
	}
}
