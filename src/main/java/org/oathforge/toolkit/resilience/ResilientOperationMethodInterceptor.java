package org.oathforge.toolkit.resilience;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.retry.support.RetryTemplate;

import org.oathforge.toolkit.exception.ResilientOperationRetryExhaustedException;

@Aspect
final class ResilientOperationMethodInterceptor {

	private static final Logger log = LoggerFactory.getLogger(ResilientOperationMethodInterceptor.class);

	private final ResilienceOperationPropertiesResolver resilienceOperationPropertiesResolver;

	private final ResilienceTimeoutExecutor resilienceTimeoutExecutor;

	private final ResilienceRetryTemplateFactory resilienceRetryTemplateFactory;

	ResilientOperationMethodInterceptor(ResilienceOperationPropertiesResolver resilienceOperationPropertiesResolver,
			ResilienceTimeoutExecutor resilienceTimeoutExecutor,
			ResilienceRetryTemplateFactory resilienceRetryTemplateFactory) {
		this.resilienceOperationPropertiesResolver = resilienceOperationPropertiesResolver;
		this.resilienceTimeoutExecutor = resilienceTimeoutExecutor;
		this.resilienceRetryTemplateFactory = resilienceRetryTemplateFactory;
	}

	@Around("@within(org.oathforge.toolkit.resilience.ResilientOperation) || @annotation(org.oathforge.toolkit.resilience.ResilientOperation)")
	public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
		ResilientOperation annotation = findAnnotation(joinPoint);
		if (annotation == null) {
			return joinPoint.proceed();
		}

		String operationName = annotation.value();
		ResolvedResilienceOperation resolvedOperation = resilienceOperationPropertiesResolver.resolve(operationName);

		log.debug("Executing resilient operation '{}' - timeout: {}, retryEnabled: {}, maxAttempts: {}, wait: {}",
				operationName, resolvedOperation.timeout(), resolvedOperation.retry().enabled(),
				resolvedOperation.retry().maxAttempts(), resolvedOperation.retry().waitDuration());

		ThrowingSupplier<Object> invocationSupplier = joinPoint::proceed;
		if (!resolvedOperation.retry().enabled()) {
			return resilienceTimeoutExecutor.execute(operationName, resolvedOperation.timeout(), invocationSupplier);
		}

		RetryTemplate retryTemplate = resilienceRetryTemplateFactory.create(operationName, resolvedOperation.retry());
		return retryTemplate.execute(
				context -> resilienceTimeoutExecutor.execute(operationName, resolvedOperation.timeout(), invocationSupplier),
				context -> {
					Throwable throwable = context.getLastThrowable();
					throw new ResilientOperationRetryExhaustedException(operationName,
							resolvedOperation.retry().maxAttempts(), throwable);
				});
	}

	private ResilientOperation findAnnotation(ProceedingJoinPoint joinPoint) {
		Class<?> targetClass = AopUtils.getTargetClass(joinPoint.getTarget());
		Method method = AopUtils.getMostSpecificMethod(((MethodSignature) joinPoint.getSignature()).getMethod(), targetClass);
		ResilientOperation methodAnnotation = AnnotationUtils.findAnnotation(method, ResilientOperation.class);
		if (methodAnnotation != null) {
			return methodAnnotation;
		}
		return AnnotationUtils.findAnnotation(targetClass, ResilientOperation.class);
	}
}
