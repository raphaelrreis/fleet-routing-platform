package dev.raphaelreis.logisticscopilot.risk.application;

import dev.raphaelreis.logisticscopilot.freight.domain.Freight;
import dev.raphaelreis.logisticscopilot.risk.domain.RouteRiskAssessment;
import dev.raphaelreis.logisticscopilot.telemetry.domain.TelemetryReading;
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

    public RouteRiskAssessment evaluate(Freight freight, TelemetryReading telemetry) {
        var assessment = detector.assess(freight, telemetry);
        if (assessment.atRisk()) {
            eventPublisher.publish(assessment);
        }
        return assessment;
    }
}

