package com.plutospace.events.domain.data.request;

import java.util.List;

public record UpdatePlanRequest(String id, String type, List<String> features, double priceNaira, double priceUsd) {
}
