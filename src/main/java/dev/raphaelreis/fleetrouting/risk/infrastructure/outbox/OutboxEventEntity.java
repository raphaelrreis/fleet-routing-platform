package dev.raphaelreis.fleetrouting.risk.infrastructure.outbox;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
public class OutboxEventEntity {

    @Id
    private UUID id;
    private UUID aggregateId;
    private String eventType;
    private Instant occurredAt;
    private String status;
    private int attempts;
    private Instant nextAttemptAt;
    @Column(length = 2000)
    private String lastError;
    @Version
    private long version;

    protected OutboxEventEntity() {
    }

    public OutboxEventEntity(UUID aggregateId, String eventType, Instant occurredAt) {
        this.id = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.status = OutboxStatus.PENDING.name();
        this.nextAttemptAt = occurredAt;
    }

    public void published() {
        status = OutboxStatus.PUBLISHED.name();
        lastError = null;
    }

    public void retryAt(Instant nextAttemptAt, String error) {
        attempts++;
        this.nextAttemptAt = nextAttemptAt;
        this.lastError = error;
    }

    public void deadLetter(String error) {
        attempts++;
        status = OutboxStatus.DEAD_LETTERED.name();
        lastError = error;
    }

    public UUID getId() { return id; }
    public UUID getAggregateId() { return aggregateId; }
    public String getStatus() { return status; }
    public int getAttempts() { return attempts; }
    public Instant getOccurredAt() { return occurredAt; }
}
