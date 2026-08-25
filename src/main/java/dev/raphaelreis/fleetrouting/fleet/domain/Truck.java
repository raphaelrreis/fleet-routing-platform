package dev.raphaelreis.fleetrouting.fleet.domain;

import java.util.Objects;
import java.util.UUID;

public record Truck(
        UUID id,
        String licensePlate,
        VehicleProfile profile,
        TruckStatus status
) {

    public Truck {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(profile, "profile must not be null");
        Objects.requireNonNull(status, "status must not be null");

        if (licensePlate == null || licensePlate.isBlank()) {
            throw new IllegalArgumentException("licensePlate must not be blank");
        }
        licensePlate = licensePlate.trim().toUpperCase();
    }

    public boolean canCarry(double weightKg, boolean requiresRefrigeration) {
        return status == TruckStatus.AVAILABLE
                && profile.capacityKg() >= weightKg
                && (!requiresRefrigeration || profile.refrigerated());
    }
}

