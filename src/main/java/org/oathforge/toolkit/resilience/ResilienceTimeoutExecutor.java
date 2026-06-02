package org.oathforge.toolkit.resilience;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.oathforge.toolkit.exception.ResilientOperationTimeoutException;
import org.slf4j.MDC;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

final class ResilienceTimeoutExecutor {

	private final ExecutorService executorService;

	ResilienceTimeoutExecutor(ExecutorService executorService) {
		this.executorService = executorService;
	}

	<T> T execute(String operationName, Duration timeout, ThrowingSupplier<T> supplier) throws Throwable {
		if (timeout == null || timeout.isZero() || timeout.isNegative()) {
			return supplier.get();
		}

		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new IllegalStateException(
					"Resilient operation '%s' cannot enforce timeout inside an active transaction. "
							.formatted(operationName)
							+ "Move the annotation to a non-transactional outbound adapter or rely on client-level timeouts.");
		}

		ExecutionContextSnapshot executionContextSnapshot = ExecutionContextSnapshot.capture();

		Future<ExecutionResult<T>> future = executorService.submit(() -> {
			try {
				return executionContextSnapshot.execute(() -> ExecutionResult.success(supplier.get()));
			} catch (Throwable throwable) {
				return ExecutionResult.failure(throwable);
			}
		});

		try {
			ExecutionResult<T> result = future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
			if (result.throwable() != null) {
				throw result.throwable();
			}
			return result.value();
		} catch (TimeoutException ex) {
			future.cancel(true);
			throw new ResilientOperationTimeoutException(operationName, timeout.toString());
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw ex;
		} catch (ExecutionException ex) {
			throw ex.getCause();
		}
	}

	private record ExecutionResult<T>(T value, Throwable throwable) {

		private static <T> ExecutionResult<T> success(T value) {
			return new ExecutionResult<>(value, null);
		}

		private static <T> ExecutionResult<T> failure(Throwable throwable) {
			return new ExecutionResult<>(null, throwable);
		}
	}

	private record ExecutionContextSnapshot(SecurityContext securityContext, RequestAttributes requestAttributes,
			LocaleContext localeContext, Map<String, String> mdcContextMap) {

		private static ExecutionContextSnapshot capture() {
			return new ExecutionContextSnapshot(SecurityContextHolder.getContext(), RequestContextHolder.getRequestAttributes(),
					LocaleContextHolder.getLocaleContext(), MDC.getCopyOfContextMap());
		}

		private <T> T execute(ThrowingSupplier<T> supplier) throws Throwable {
			SecurityContext previousSecurityContext = SecurityContextHolder.getContext();
			RequestAttributes previousRequestAttributes = RequestContextHolder.getRequestAttributes();
			LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
			Map<String, String> previousMdcContextMap = MDC.getCopyOfContextMap();

			try {
				SecurityContextHolder.setContext(securityContext);
				if (requestAttributes != null) {
					RequestContextHolder.setRequestAttributes(requestAttributes);
				} else {
					RequestContextHolder.resetRequestAttributes();
				}
				LocaleContextHolder.setLocaleContext(localeContext);
				if (mdcContextMap != null) {
					MDC.setContextMap(mdcContextMap);
				} else {
					MDC.clear();
				}
				return supplier.get();
			} finally {
				SecurityContextHolder.setContext(previousSecurityContext);
				if (previousRequestAttributes != null) {
					RequestContextHolder.setRequestAttributes(previousRequestAttributes);
				} else {
					RequestContextHolder.resetRequestAttributes();
				}
				LocaleContextHolder.setLocaleContext(previousLocaleContext);
				if (previousMdcContextMap != null) {
					MDC.setContextMap(previousMdcContextMap);
				} else {
					MDC.clear();
				}
			}
		}
	}
}
