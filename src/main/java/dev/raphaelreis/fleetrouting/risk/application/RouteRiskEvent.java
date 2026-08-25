package dev.raphaelreis.fleetrouting.risk.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RouteRiskEvent(
        UUID assessmentId,
        String cellId,
        UUID freightId,
        UUID truckId,
        Instant assessedAt,
        String severity,
        List<String> reasons
) {
}
