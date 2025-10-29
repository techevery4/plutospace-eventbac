/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.entities;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = -6342630857637389028L;

	@Id
	private String id;

	@CreatedDate
	private LocalDateTime createdOn;

	@LastModifiedDate
	private LocalDateTime updatedOn;

	@CreatedBy
	private String createdBy;

	@LastModifiedBy
	private String updatedBy;
}
