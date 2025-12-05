/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

public record CreateProposalSubmissionRequest(String name, String email, String countryCode, String phoneNumber,
		String mediaId, String mediaUrl) {
}
