package dev.raphaelreis.fleetrouting.risk.application;

import dev.raphaelreis.fleetrouting.risk.infrastructure.outbox.OutboxEventEntity;
import dev.raphaelreis.fleetrouting.risk.infrastructure.outbox.OutboxEventJpaRepository;
import dev.raphaelreis.fleetrouting.risk.infrastructure.persistence.RouteRiskAssessmentEntity;
import dev.raphaelreis.fleetrouting.risk.infrastructure.persistence.RouteRiskAssessmentJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvaluateRouteRiskCommandHandler {

    private final RouteRiskDetector detector;
    private final RouteRiskAssessmentJpaRepository assessmentRepository;
    private final OutboxEventJpaRepository outboxRepository;

    public EvaluateRouteRiskCommandHandler(
            RouteRiskDetector detector,
            RouteRiskAssessmentJpaRepository assessmentRepository,
            OutboxEventJpaRepository outboxRepository
    ) {
        this.detector = detector;
        this.assessmentRepository = assessmentRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public RouteRiskAssessmentReceipt handle(EvaluateRouteRiskCommand command) {
        var assessment = detector.assess(command.cellId(), command.freight(), command.telemetry());
        assessmentRepository.save(new RouteRiskAssessmentEntity(assessment));
        outboxRepository.save(new OutboxEventEntity(
                assessment.assessmentId(),
                "RouteRiskDetected",
                assessment.assessedAt()
        ));
        return new RouteRiskAssessmentReceipt(
                assessment.assessmentId(),
                assessment.severity(),
                assessment.atRisk()
        );
    }
}
