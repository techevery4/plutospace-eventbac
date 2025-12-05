/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plutospace.events.domain.entities.ProposalSubmission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "instance")
public class ProposalSubmissionResponse {

	private String id;
	private String proposalId;
	private String name;
	private String email;
	private ProposalSubmission.PhoneNumber phoneNumber;
	private Boolean calledForPresentation;
	private String mediaId;
	private String mediaUrl;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
}
