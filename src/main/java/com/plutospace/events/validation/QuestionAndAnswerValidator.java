/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.plutospace.events.commons.exception.GeneralPlatformDomainRuleException;
import com.plutospace.events.domain.data.request.*;

@Component
public class QuestionAndAnswerValidator {

	public void validate(CreateQuestionAndAnswerRequest createQuestionAndAnswerRequest) {
		String titleCannotBeNullValidationMessage = "Title cannot be empty";

		if (StringUtils.isBlank(createQuestionAndAnswerRequest.title())) {
			throw new GeneralPlatformDomainRuleException(titleCannotBeNullValidationMessage);
		}
	}

	public void validate(AskQuestionRequest askQuestionRequest) {
		String nameCannotBeNullValidationMessage = "Name cannot be empty";
		String questionCannotBeNullValidationMessage = "Question cannot be null";

		if (StringUtils.isBlank(askQuestionRequest.question())) {
			throw new GeneralPlatformDomainRuleException(questionCannotBeNullValidationMessage);
		}
		if (StringUtils.isBlank(askQuestionRequest.name())) {
			throw new GeneralPlatformDomainRuleException(nameCannotBeNullValidationMessage);
		}
	}

	public void validate(AnswerQuestionRequest answerQuestionRequest) {
		String answerCannotBeNullValidationMessage = "Answer cannot be null";

		if (StringUtils.isBlank(answerQuestionRequest.answer())) {
			throw new GeneralPlatformDomainRuleException(answerCannotBeNullValidationMessage);
		}
	}
}
