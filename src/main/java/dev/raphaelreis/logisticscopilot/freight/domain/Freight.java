package dev.raphaelreis.logisticscopilot.freight.domain;

import dev.raphaelreis.logisticscopilot.shared.domain.GeoPoint;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Freight(
        UUID id,
        double weightKg,
        boolean requiresRefrigeration,
        Double maximumCargoTemperatureCelsius,
        FreightPriority priority,
        FreightStatus status,
        GeoPoint origin,
        GeoPoint destination,
        Instant deliveryDeadline,
        UUID assignedTruckId
) {

    public Freight {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(priority, "priority must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(origin, "origin must not be null");
        Objects.requireNonNull(destination, "destination must not be null");
        Objects.requireNonNull(deliveryDeadline, "deliveryDeadline must not be null");

        if (weightKg <= 0) {
            throw new IllegalArgumentException("weightKg must be greater than zero");
        }
        if (requiresRefrigeration && maximumCargoTemperatureCelsius == null) {
            throw new IllegalArgumentException("refrigerated freight requires a maximum cargo temperature");
        }
    }
}

