package com.plutospace.events.validation;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.CreatePlanRequest;
import com.plutospace.events.domain.data.request.UpdatePlanRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class PlanValidator {

    public void validate(CreatePlanRequest request) {
        String typeCannotBeNullValidationMessage = "type cannot be null";
        String featuresCannotBeNullValidationMessage = "features cannot be null";
        String priceNairaCannotBeNullValidationMessage = "priceNaira cannot be null";
        String priceUsdCannotBeNullValidationMessage = "priceUsd cannot be null";

        if (StringUtils.isBlank(request.type())) {
            throw new GeneralPlatformDomainRuleException(typeCannotBeNullValidationMessage);
        }
        if (ObjectUtils.isEmpty(request.features())) {
            throw new GeneralPlatformDomainRuleException(featuresCannotBeNullValidationMessage);
        }
        if (request.priceNaira() <= 0) {
            throw new GeneralPlatformDomainRuleException(priceNairaCannotBeNullValidationMessage);
        }
        if (request.priceUsd() <= 0) {
            throw new GeneralPlatformDomainRuleException(priceUsdCannotBeNullValidationMessage);
        }
    }

    public void validate(UpdatePlanRequest request) {
        String idCannotBeNullValidationMessage = "id cannot be null";
        String typeCannotBeNullValidationMessage = "type cannot be null";
        String featuresCannotBeNullValidationMessage = "features cannot be null";
        String priceNairaCannotBeNullValidationMessage = "priceNaira cannot be null";
        String priceUsdCannotBeNullValidationMessage = "priceUsd cannot be null";

        if (StringUtils.isBlank(request.id())) {
            throw new GeneralPlatformDomainRuleException(idCannotBeNullValidationMessage);
        }
        if (StringUtils.isBlank(request.type())) {
            throw new GeneralPlatformDomainRuleException(typeCannotBeNullValidationMessage);
        }
        if (ObjectUtils.isEmpty(request.features())) {
            throw new GeneralPlatformDomainRuleException(featuresCannotBeNullValidationMessage);
        }
        if (request.priceNaira() <= 0) {
            throw new GeneralPlatformDomainRuleException(priceNairaCannotBeNullValidationMessage);
        }
        if (request.priceUsd() <= 0) {
            throw new GeneralPlatformDomainRuleException(priceUsdCannotBeNullValidationMessage);
        }
    }
}
