package dev.raphaelreis.fleetrouting.risk.infrastructure.outbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("fleet-routing.outbox.retry")
public record OutboxRetryProperties(
        int maxAttempts,
        Duration initialDelay,
        double multiplier,
        Duration maxDelay
) {
}
