package dev.raphaelreis.logisticscopilot.risk.application;

import dev.raphaelreis.logisticscopilot.freight.domain.Freight;
import dev.raphaelreis.logisticscopilot.freight.domain.FreightPriority;
import dev.raphaelreis.logisticscopilot.freight.domain.FreightStatus;
import dev.raphaelreis.logisticscopilot.risk.domain.RiskReason;
import dev.raphaelreis.logisticscopilot.risk.domain.RiskSeverity;
import dev.raphaelreis.logisticscopilot.shared.domain.GeoPoint;
import dev.raphaelreis.logisticscopilot.telemetry.domain.TelemetryReading;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RouteRiskDetectorTest {

    private final RouteRiskDetector detector = new RouteRiskDetector();
    private final UUID truckId = UUID.randomUUID();
    private final UUID freightId = UUID.randomUUID();

    @Test
    void classifiesTemperatureViolationOnCriticalFreightAsCritical() {
        var assessment = detector.assess(criticalRefrigeratedFreight(), telemetry(9, 8.4, 25, 65));

        assertThat(assessment.severity()).isEqualTo(RiskSeverity.CRITICAL);
        assertThat(assessment.reasons()).contains(
                RiskReason.CARGO_TEMPERATURE_EXCEEDED,
                RiskReason.LOW_FUEL,
                RiskReason.DELIVERY_DELAY
        );
    }

    @Test
    void returnsNoRiskForHealthyTelemetry() {
        var assessment = detector.assess(criticalRefrigeratedFreight(), telemetry(70, 3.5, 0, 72));

        assertThat(assessment.severity()).isEqualTo(RiskSeverity.NONE);
        assertThat(assessment.reasons()).isEmpty();
        assertThat(assessment.atRisk()).isFalse();
    }

    private Freight criticalRefrigeratedFreight() {
        return new Freight(
                freightId,
                8_000,
                true,
                5.0,
                FreightPriority.CRITICAL,
                FreightStatus.IN_TRANSIT,
                new GeoPoint(-15.793889, -47.882778),
                new GeoPoint(-16.686891, -49.264794),
                Instant.now().plus(2, ChronoUnit.HOURS),
                truckId
        );
    }

    private TelemetryReading telemetry(
            double fuelPercentage,
            double cargoTemperature,
            int estimatedDelayMinutes,
            double speedKph
    ) {
        return new TelemetryReading(
                truckId,
                Instant.now(),
                new GeoPoint(-16.020000, -48.100000),
                speedKph,
                fuelPercentage,
                cargoTemperature,
                estimatedDelayMinutes
        );
    }
}

