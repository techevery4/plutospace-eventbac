/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.domain.data.request;

import java.util.List;

public record CreatePlanRequest(String type, String name, List<String> features, double priceNaira, double priceUsd) {
}
