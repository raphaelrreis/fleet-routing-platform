package dev.raphaelreis.logisticscopilot.risk.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record RouteRiskAssessment(
        UUID freightId,
        UUID truckId,
        Instant assessedAt,
        RiskSeverity severity,
        Set<RiskReason> reasons
) {

    public RouteRiskAssessment {
        Objects.requireNonNull(freightId, "freightId must not be null");
        Objects.requireNonNull(truckId, "truckId must not be null");
        Objects.requireNonNull(assessedAt, "assessedAt must not be null");
        Objects.requireNonNull(severity, "severity must not be null");
        reasons = Set.copyOf(Objects.requireNonNull(reasons, "reasons must not be null"));

        if (severity == RiskSeverity.NONE && !reasons.isEmpty()) {
            throw new IllegalArgumentException("NONE severity cannot contain risk reasons");
        }
        if (severity != RiskSeverity.NONE && reasons.isEmpty()) {
            throw new IllegalArgumentException("a risk severity requires at least one reason");
        }
    }

    public boolean atRisk() {
        return severity != RiskSeverity.NONE;
    }
}

