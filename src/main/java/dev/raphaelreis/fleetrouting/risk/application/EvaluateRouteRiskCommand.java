package dev.raphaelreis.fleetrouting.risk.application;

import dev.raphaelreis.fleetrouting.freight.domain.Freight;
import dev.raphaelreis.fleetrouting.shared.domain.CellId;
import dev.raphaelreis.fleetrouting.telemetry.domain.TelemetryReading;

public record EvaluateRouteRiskCommand(CellId cellId, Freight freight, TelemetryReading telemetry) {
}
