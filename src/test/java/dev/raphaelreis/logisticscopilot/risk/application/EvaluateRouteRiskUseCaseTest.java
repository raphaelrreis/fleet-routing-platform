package dev.raphaelreis.logisticscopilot.risk.application;

import dev.raphaelreis.logisticscopilot.freight.domain.Freight;
import dev.raphaelreis.logisticscopilot.freight.domain.FreightPriority;
import dev.raphaelreis.logisticscopilot.freight.domain.FreightStatus;
import dev.raphaelreis.logisticscopilot.risk.domain.RouteRiskAssessment;
import dev.raphaelreis.logisticscopilot.shared.domain.GeoPoint;
import dev.raphaelreis.logisticscopilot.telemetry.domain.TelemetryReading;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluateRouteRiskUseCaseTest {

    @Test
    void publishesOnlyAssessmentsThatContainRisk() {
        var published = new ArrayList<RouteRiskAssessment>();
        var useCase = new EvaluateRouteRiskUseCase(new RouteRiskDetector(), published::add);
        var truckId = UUID.randomUUID();
        var freight = freightAssignedTo(truckId);

        useCase.evaluate(freight, telemetry(truckId, 80, 4.0, 0));
        useCase.evaluate(freight, telemetry(truckId, 8, 7.5, 45));

        assertThat(published).hasSize(1);
        assertThat(published.getFirst().atRisk()).isTrue();
    }

    private static Freight freightAssignedTo(UUID truckId) {
        return new Freight(
                UUID.randomUUID(),
                5_000,
                true,
                5.0,
                FreightPriority.EXPRESS,
                FreightStatus.IN_TRANSIT,
                new GeoPoint(-15.793889, -47.882778),
                new GeoPoint(-16.686891, -49.264794),
                Instant.now().plus(2, ChronoUnit.HOURS),
                truckId
        );
    }

    private static TelemetryReading telemetry(
            UUID truckId,
            double fuel,
            double temperature,
            int delay
    ) {
        return new TelemetryReading(
                truckId,
                Instant.now(),
                new GeoPoint(-16.020000, -48.100000),
                65,
                fuel,
                temperature,
                delay
        );
    }
}
