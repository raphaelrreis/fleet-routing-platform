package dev.raphaelreis.fleetrouting.risk.application;

import dev.raphaelreis.fleetrouting.freight.domain.Freight;
import dev.raphaelreis.fleetrouting.risk.domain.RouteRiskAssessment;
import dev.raphaelreis.fleetrouting.shared.domain.CellId;
import dev.raphaelreis.fleetrouting.telemetry.domain.TelemetryReading;
import org.springframework.stereotype.Service;

@Service
public class EvaluateRouteRiskUseCase {

    private final RouteRiskDetector detector;
    private final RouteRiskEventPublisher eventPublisher;

    public EvaluateRouteRiskUseCase(
            RouteRiskDetector detector,
            RouteRiskEventPublisher eventPublisher
    ) {
        this.detector = detector;
        this.eventPublisher = eventPublisher;
    }

    public RouteRiskAssessment evaluate(CellId cellId, Freight freight, TelemetryReading telemetry) {
        var assessment = detector.assess(cellId, freight, telemetry);
        if (assessment.atRisk()) {
            eventPublisher.publish(assessment);
        }
        return assessment;
    }
}
