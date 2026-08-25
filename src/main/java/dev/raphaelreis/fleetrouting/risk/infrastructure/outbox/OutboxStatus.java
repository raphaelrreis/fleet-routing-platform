package dev.raphaelreis.fleetrouting.risk.infrastructure.outbox;

public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    DEAD_LETTERED
}
