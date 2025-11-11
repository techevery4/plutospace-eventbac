/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.entities;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.plutospace.events.commons.entities.BaseEntity;
import com.plutospace.events.domain.data.AdminRoleType;
import com.plutospace.events.domain.data.AdminUserStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "instance")
public class AdminUser extends BaseEntity {

	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private Boolean isPendingUser;
	private AdminRoleType roleType;
	private LocalDateTime lastLogin;
	private AdminUserStatus status;
}
