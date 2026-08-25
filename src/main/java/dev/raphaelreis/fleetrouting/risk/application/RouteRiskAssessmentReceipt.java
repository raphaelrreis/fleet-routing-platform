package dev.raphaelreis.fleetrouting.risk.application;

import dev.raphaelreis.fleetrouting.risk.domain.RiskSeverity;

import java.util.UUID;

public record RouteRiskAssessmentReceipt(UUID assessmentId, RiskSeverity severity, boolean atRisk) {
}
