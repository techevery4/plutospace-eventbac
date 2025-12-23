/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class AccountUser extends BaseEntity {

	private String accountId;

	private String firstName;
	private String lastName;
	private String name; // this is stored for business type

	private String email;
	private String password;

	private String imageId;
	private String imageUrl;

	private LocalDateTime lastLogin;

	private Boolean isActive;
}
