/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;

import lombok.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class ProposalSubmission extends BaseEntity {

	private String proposalId;
	private String name;
	private String email;
	private PhoneNumber phoneNumber;
	private Boolean calledForPresentation;
	private String mediaId;
	private String mediaUrl;

	@Data
	public static class PhoneNumber {
		private String countryCode;
		private String number;
	}
}
