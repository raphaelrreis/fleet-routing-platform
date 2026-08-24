package dev.raphaelreis.logisticscopilot.telemetry.domain;

import dev.raphaelreis.logisticscopilot.shared.domain.GeoPoint;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record TelemetryReading(
        UUID truckId,
        Instant recordedAt,
        GeoPoint position,
        double speedKph,
        double fuelPercentage,
        Double cargoTemperatureCelsius,
        int estimatedDelayMinutes
) {

    public TelemetryReading {
        Objects.requireNonNull(truckId, "truckId must not be null");
        Objects.requireNonNull(recordedAt, "recordedAt must not be null");
        Objects.requireNonNull(position, "position must not be null");

        if (speedKph < 0) {
            throw new IllegalArgumentException("speedKph must not be negative");
        }
        if (fuelPercentage < 0 || fuelPercentage > 100) {
            throw new IllegalArgumentException("fuelPercentage must be between 0 and 100");
        }
        if (estimatedDelayMinutes < 0) {
            throw new IllegalArgumentException("estimatedDelayMinutes must not be negative");
        }
    }
}

