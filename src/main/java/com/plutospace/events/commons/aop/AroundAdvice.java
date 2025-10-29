/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;

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

	@Around("execution(* com.plutospace.events.controllers.*.*(..))")
	public Object authenticationAndLogging(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("Checking in loan service");

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

		Object result = joinPoint.proceed();

		log.info("After executing method ");

		return result;
	}
}
