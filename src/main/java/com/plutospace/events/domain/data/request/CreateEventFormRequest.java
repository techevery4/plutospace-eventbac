/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.util.List;

public record CreateEventFormRequest(String title, String type, List<String> options, Boolean isRequired) {
}
