package dev.raphaelreis.fleetrouting.telemetry.infrastructure.acl;

import java.time.Instant;

public record TelematicsPayload(
        Instant observedAt,
        double latitude,
        double longitude,
        double speedMetersPerSecond,
        double fuelLevelRatio,
        Double cargoTemperatureCelsius,
        int estimatedDelaySeconds
) {
}
