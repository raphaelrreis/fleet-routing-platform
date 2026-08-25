package dev.raphaelreis.fleetrouting.fleet.domain;

public record VehicleProfile(
        double capacityKg,
        double heightMeters,
        double widthMeters,
        double lengthMeters,
        boolean refrigerated
) {

    public VehicleProfile {
        requirePositive(capacityKg, "capacityKg");
        requirePositive(heightMeters, "heightMeters");
        requirePositive(widthMeters, "widthMeters");
        requirePositive(lengthMeters, "lengthMeters");
    }

    private static void requirePositive(double value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be greater than zero");
        }
    }
}

