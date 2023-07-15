package io.jpdev01.dynamodbenhanced

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import org.assertj.core.api.Assert
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class RateLimiterTest {

    @Test
    public void testLimiteUltrapassado() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(4)
            .limitRefreshPeriod(Duration.ofSeconds(6))
            .timeoutDuration(Duration.ofSeconds(1))
            .build()
        RateLimiter rateLimiter = RateLimiter.of("ApiRateLimiter", config)

        for (int i = 0; i < 4; i++) {
            rateLimiter.executeRunnable(() -> println "call ${i}")
        }

        Assertions.assertThatExceptionOfType(RequestNotPermitted.class)
            .isThrownBy(() ->
                rateLimiter.executeRunnable(() -> println "olá")
            )

//        String result = rateLimiter.executeSupplier {
//                return String.join("a", "b")
//            }
//        CompletionStage<String> stage = rateLimiter.executeCompletionStage {
//            return CompletableFuture
//                .supplyAsync {
//                    String.join("a", "b")
//                }
//        }
//
//        String result2 = stage.toCompletableFuture().join()
    }

    @Test
    void testTimeoutNaoUltrapassado() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(4)
            .limitRefreshPeriod(Duration.ofSeconds(6))
            .timeoutDuration(Duration.ofSeconds(1))
            .build()
        RateLimiter rateLimiter = RateLimiter.of("ApiRateLimiter", config)

        for (int i = 0; i < 4; i++) {
            rateLimiter.executeRunnable(() -> println "call ${i}")
        }

        Thread.sleep(Duration.ofSeconds(5).toMillis()) // deve passar pois executou o refresh
        rateLimiter.executeRunnable(() -> println "olá")
    }

    @Test
    void testRegistryInstances() { // controla a instancia dos rate limiters
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(4)
            .limitRefreshPeriod(Duration.ofSeconds(6))
            .timeoutDuration(Duration.ofSeconds(1))
            .build()

        RateLimiterRegistry registry = RateLimiterRegistry.of(config)

        RateLimiter rateLimiter = registry.rateLimiter("TestRateLimiter")
        RateLimiter rateLimiter2 = registry.rateLimiter("TestRateLimiter")

        Assertions.assertThat(rateLimiter).isEqualTo(rateLimiter2)
    }
}
