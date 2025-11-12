/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plutospace.events.commons.definitions.GeneralConstants;
import com.plutospace.events.commons.definitions.PropertyConstants;
import com.plutospace.events.commons.exception.UnauthorizedAccessException;
import com.plutospace.events.commons.utils.SecurityMapper;
import com.plutospace.events.domain.data.response.OperationalResponse;
import com.plutospace.events.services.AccountSessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor
public class AroundAdvice {

	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final SecurityMapper securityMapper;
	private final PropertyConstants propertyConstants;
	private final AccountSessionService accountSessionService;

	@Around("execution(* com.plutospace.events.controllers.*.*(..)) && !execution(* com.plutospace.events.controllers.AccountUserApiResource.registerPersonalAccount(..))"
			+ "&& !execution(* com.plutospace.events.controllers.AccountUserApiResource.registerBusinessAccount(..)) && !execution(* com.plutospace.events.controllers.AccountUserApiResource.login(..))"
			+ "&& !execution(* com.plutospace.events.controllers.AdminUserApiResource.createAdminUser(..)) && !execution(* com.plutospace.events.controllers.AdminUserApiResource.login(..))"
			+ "&& !execution(* com.plutospace.events.controllers.MeetingApiResource.retrieveMeetingByPublicId(..)) && !execution(* com.plutospace.events.controllers.PlanApiResource.retrievePlan(..))"
			+ "&& !execution(* com.plutospace.events.controllers.PlanApiResource.retrieveAllPlans(..))")
	public Object authenticationAndLogging(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("Checking in plutospace events");

		Method method = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod();
		RequestMapping requestMapping = AnnotatedElementUtils.getMergedAnnotation(method, RequestMapping.class);
		String path = "";
		if (requestMapping != null && requestMapping.value().length > 0) {
			path = requestMapping.value()[0];
		}
		String[] urlParts = request.getServletPath().split("/");
		String url = "/" + urlParts[1] + "/" + urlParts[2] + "/" + urlParts[3] + path;
		String verb = request.getMethod();
		log.info("url {}", url);
		log.info("verb {}", verb);

		if (request.getHeader(GeneralConstants.TOKEN_KEY) == null)
			throw new UnauthorizedAccessException(
					"You cannot complete this request as necessary credentials are missing. Kindly login again");
		String token = request.getHeader(GeneralConstants.TOKEN_KEY);
		String decryptedToken = securityMapper.extractDetailsFromLoginToken(token,
				propertyConstants.getEventsLoginEncryptionSecretKey());
		String[] words = decryptedToken.split(":");
		if (words.length < 3)
			throw new UnauthorizedAccessException("Your session has expired. Kindly login again to restart session");

		if (words.length == 4) {
			String userAgent = request.getHeader("User-Agent");
			OperationalResponse operationalResponse = accountSessionService.validateToken(token, userAgent);
			log.info("Response {}", operationalResponse.getMessage());
		}

		Object result = joinPoint.proceed();

		log.info("After executing method ");

		return result;
	}
}
