package dev.raphaelreis.fleetrouting.risk.domain;

import dev.raphaelreis.fleetrouting.shared.domain.CellId;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record RouteRiskAssessment(
        CellId cellId,
        UUID freightId,
        UUID truckId,
        Instant assessedAt,
        RiskSeverity severity,
        Set<RiskReason> reasons
) {

    public RouteRiskAssessment {
        Objects.requireNonNull(cellId, "cellId must not be null");
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
