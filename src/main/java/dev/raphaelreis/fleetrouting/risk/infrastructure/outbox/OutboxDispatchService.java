package dev.raphaelreis.fleetrouting.risk.infrastructure.outbox;

import dev.raphaelreis.fleetrouting.recommendation.application.RouteRecommendationGenerator;
import dev.raphaelreis.fleetrouting.risk.application.RouteRiskEvent;
import dev.raphaelreis.fleetrouting.risk.application.RouteRiskEventPublisher;
import dev.raphaelreis.fleetrouting.risk.infrastructure.persistence.RouteRiskAssessmentEntity;
import dev.raphaelreis.fleetrouting.risk.infrastructure.persistence.RouteRiskAssessmentJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OutboxDispatchService {

    private final OutboxEventJpaRepository outboxRepository;
    private final RouteRiskAssessmentJpaRepository assessmentRepository;
    private final RouteRiskEventPublisher eventPublisher;
    private final RouteRecommendationGenerator recommendationGenerator;
    private final OutboxRetryProperties retry;
    private final Clock clock = Clock.systemUTC();

    public OutboxDispatchService(
            OutboxEventJpaRepository outboxRepository,
            RouteRiskAssessmentJpaRepository assessmentRepository,
            RouteRiskEventPublisher eventPublisher,
            RouteRecommendationGenerator recommendationGenerator,
            OutboxRetryProperties retry
    ) {
        this.outboxRepository = outboxRepository;
        this.assessmentRepository = assessmentRepository;
        this.eventPublisher = eventPublisher;
        this.recommendationGenerator = recommendationGenerator;
        this.retry = retry;
    }

    @Transactional
    public void dispatch(UUID outboxId) {
        var outbox = outboxRepository.findById(outboxId).orElseThrow();
        if (!OutboxStatus.PENDING.name().equals(outbox.getStatus())) return;

        var assessment = assessmentRepository.findById(outbox.getAggregateId()).orElseThrow();
        var event = toEvent(assessment);
        try {
            var recommendation = recommendationGenerator.generate(event);
            assessment.completeRecommendation(
                    recommendation.recommendation(),
                    recommendation.rationale(),
                    String.join(",", recommendation.requiredActions())
            );
            eventPublisher.publish(event);
            outbox.published();
        } catch (RuntimeException exception) {
            if (outbox.getAttempts() + 1 >= retry.maxAttempts()) {
                outbox.deadLetter(limit(exception.getMessage()));
                assessment.failRecommendation();
            } else {
                outbox.retryAt(clock.instant().plus(backoff(outbox.getAttempts())), limit(exception.getMessage()));
            }
        }
    }

    private Duration backoff(int completedAttempts) {
        var calculated = retry.initialDelay().multipliedBy((long) Math.pow(retry.multiplier(), completedAttempts));
        return calculated.compareTo(retry.maxDelay()) > 0 ? retry.maxDelay() : calculated;
    }

    private static RouteRiskEvent toEvent(RouteRiskAssessmentEntity entity) {
        return new RouteRiskEvent(
                entity.getId(), entity.getCellId(), entity.getFreightId(), entity.getTruckId(),
                entity.getAssessedAt(), entity.getSeverity(), split(entity.getReasons())
        );
    }

    private static List<String> split(String value) {
        return value == null || value.isBlank() ? List.of() : Arrays.asList(value.split(","));
    }

    private static String limit(String value) {
        if (value == null) return "Unknown publication failure";
        return value.length() <= 2000 ? value : value.substring(0, 2000);
    }
}
