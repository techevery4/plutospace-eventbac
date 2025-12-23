/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoMailerRequest {

	private String template_code;
	private String html;
	private String recipient_email;
	private String bcc;
	private MailData data;

	@Data
	public static class MailData {
		private String firstName;
		private String company;
		private String invitationLink;
		private int year;
		private String supportEmail;
	}
}
