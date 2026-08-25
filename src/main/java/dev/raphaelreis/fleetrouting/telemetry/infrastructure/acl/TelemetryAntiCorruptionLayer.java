package dev.raphaelreis.fleetrouting.telemetry.infrastructure.acl;

import dev.raphaelreis.fleetrouting.shared.domain.GeoPoint;
import dev.raphaelreis.fleetrouting.telemetry.domain.TelemetryReading;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TelemetryAntiCorruptionLayer {

    public TelemetryReading toDomain(UUID truckId, TelematicsPayload payload) {
        return new TelemetryReading(
                truckId,
                payload.observedAt(),
                new GeoPoint(payload.latitude(), payload.longitude()),
                payload.speedMetersPerSecond() * 3.6,
                payload.fuelLevelRatio() * 100,
                payload.cargoTemperatureCelsius(),
                Math.max(0, (int) Math.ceil(payload.estimatedDelaySeconds() / 60.0))
        );
    }
}
