package com.plutospace.events.domain.data.request;

import java.util.List;

public record CreatePlanRequest(String type, List<String> features, double priceNaira, double priceUsd) {
}
