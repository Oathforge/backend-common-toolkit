package org.oathforge.toolkit.resilience;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ConditionalOnProperty(prefix = "backend-toolkit.resilience", name = "enabled", havingValue = "true",
		matchIfMissing = false)
@EnableConfigurationProperties(ResilienceProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ResilienceConfiguration {

	@Bean(destroyMethod = "shutdown")
	ExecutorService resilienceExecutorService() {
		AtomicInteger counter = new AtomicInteger();
		ThreadFactory threadFactory = runnable -> {
			Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			thread.setName("backend-toolkit-resilience-" + counter.incrementAndGet());
			return thread;
		};
		return Executors.newCachedThreadPool(threadFactory);
	}

	@Bean
	ResilienceOperationPropertiesResolver resilienceOperationPropertiesResolver(ResilienceProperties properties) {
		return new ResilienceOperationPropertiesResolver(properties);
	}

	@Bean
	ResilienceTimeoutExecutor resilienceTimeoutExecutor(ExecutorService resilienceExecutorService) {
		return new ResilienceTimeoutExecutor(resilienceExecutorService);
	}

	@Bean
	ResilienceRetryTemplateFactory resilienceRetryTemplateFactory() {
		return new ResilienceRetryTemplateFactory();
	}

	@Bean
	ResilientOperationMethodInterceptor resilientOperationMethodInterceptor(
			ResilienceOperationPropertiesResolver resilienceOperationPropertiesResolver,
			ResilienceTimeoutExecutor resilienceTimeoutExecutor,
			ResilienceRetryTemplateFactory resilienceRetryTemplateFactory) {
		return new ResilientOperationMethodInterceptor(resilienceOperationPropertiesResolver, resilienceTimeoutExecutor,
				resilienceRetryTemplateFactory);
	}
}
