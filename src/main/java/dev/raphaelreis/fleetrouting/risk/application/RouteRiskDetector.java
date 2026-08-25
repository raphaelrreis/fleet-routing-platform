package dev.raphaelreis.fleetrouting.risk.application;

import dev.raphaelreis.fleetrouting.freight.domain.Freight;
import dev.raphaelreis.fleetrouting.freight.domain.FreightPriority;
import dev.raphaelreis.fleetrouting.risk.domain.RiskReason;
import dev.raphaelreis.fleetrouting.risk.domain.RiskSeverity;
import dev.raphaelreis.fleetrouting.risk.domain.RouteRiskAssessment;
import dev.raphaelreis.fleetrouting.shared.domain.CellId;
import dev.raphaelreis.fleetrouting.telemetry.domain.TelemetryReading;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Objects;
import java.nio.charset.StandardCharsets;

@Service
public class RouteRiskDetector {

    private static final double LOW_FUEL_THRESHOLD = 15.0;
    private static final double STOPPED_SPEED_THRESHOLD_KPH = 5.0;

    public RouteRiskAssessment assess(CellId cellId, Freight freight, TelemetryReading telemetry) {
        Objects.requireNonNull(cellId, "cellId must not be null");
        Objects.requireNonNull(freight, "freight must not be null");
        Objects.requireNonNull(telemetry, "telemetry must not be null");

        ensureTelemetryBelongsToAssignedTruck(freight, telemetry);

        var reasons = EnumSet.noneOf(RiskReason.class);

        if (telemetry.fuelPercentage() < LOW_FUEL_THRESHOLD) {
            reasons.add(RiskReason.LOW_FUEL);
        }
        if (cargoTemperatureExceeded(freight, telemetry)) {
            reasons.add(RiskReason.CARGO_TEMPERATURE_EXCEEDED);
        }
        if (telemetry.estimatedDelayMinutes() > delayTolerance(freight.priority())) {
            reasons.add(RiskReason.DELIVERY_DELAY);
        }
        if (telemetry.speedKph() < STOPPED_SPEED_THRESHOLD_KPH
                && telemetry.estimatedDelayMinutes() > 0) {
            reasons.add(RiskReason.VEHICLE_STOPPED);
        }

        return new RouteRiskAssessment(
                assessmentId(cellId, freight, telemetry),
                cellId,
                freight.id(),
                telemetry.truckId(),
                telemetry.recordedAt(),
                severityFor(freight, reasons),
                reasons
        );
    }

    private static java.util.UUID assessmentId(CellId cellId, Freight freight, TelemetryReading telemetry) {
        var key = "%s:%s:%s".formatted(cellId, freight.id(), telemetry.recordedAt());
        return java.util.UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }

    private static void ensureTelemetryBelongsToAssignedTruck(Freight freight, TelemetryReading telemetry) {
        if (freight.assignedTruckId() == null) {
            throw new IllegalArgumentException("freight must have an assigned truck");
        }
        if (!freight.assignedTruckId().equals(telemetry.truckId())) {
            throw new IllegalArgumentException("telemetry does not belong to the truck assigned to the freight");
        }
    }

    private static boolean cargoTemperatureExceeded(Freight freight, TelemetryReading telemetry) {
        return freight.requiresRefrigeration()
                && telemetry.cargoTemperatureCelsius() != null
                && telemetry.cargoTemperatureCelsius() > freight.maximumCargoTemperatureCelsius();
    }

    private static int delayTolerance(FreightPriority priority) {
        return switch (priority) {
            case STANDARD -> 60;
            case EXPRESS -> 30;
            case CRITICAL -> 10;
        };
    }

    private static RiskSeverity severityFor(Freight freight, EnumSet<RiskReason> reasons) {
        if (reasons.isEmpty()) {
            return RiskSeverity.NONE;
        }
        if (reasons.contains(RiskReason.CARGO_TEMPERATURE_EXCEEDED)
                && freight.priority() == FreightPriority.CRITICAL) {
            return RiskSeverity.CRITICAL;
        }
        if (reasons.contains(RiskReason.CARGO_TEMPERATURE_EXCEEDED) || reasons.size() >= 3) {
            return RiskSeverity.HIGH;
        }
        if (reasons.size() == 2 || reasons.contains(RiskReason.DELIVERY_DELAY)) {
            return RiskSeverity.MEDIUM;
        }
        return RiskSeverity.LOW;
    }
}
