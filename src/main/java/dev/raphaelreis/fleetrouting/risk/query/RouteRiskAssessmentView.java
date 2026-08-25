package dev.raphaelreis.fleetrouting.risk.query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RouteRiskAssessmentView(
        UUID assessmentId,
        String cellId,
        UUID freightId,
        UUID truckId,
        Instant assessedAt,
        String severity,
        List<String> reasons,
        boolean atRisk,
        String recommendationStatus,
        String recommendation,
        String rationale,
        List<String> requiredActions
) {
}
