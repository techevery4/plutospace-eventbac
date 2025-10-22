package com.plutospace.events.domain.data;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;

public enum PlanType {

    PERSONAL, BUSINESS;

    public static PlanType fromValue(String value) {
        for (PlanType planType : PlanType.values()) {
            if (planType.toString().equalsIgnoreCase(value)) {
                return planType;
            }
        }
        throw new GeneralPlatformDomainRuleException("Invalid plan type: " + value);
    }
}
