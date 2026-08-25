package dev.raphaelreis.fleetrouting.risk.application;

import dev.raphaelreis.fleetrouting.freight.domain.Freight;
import dev.raphaelreis.fleetrouting.freight.domain.FreightPriority;
import dev.raphaelreis.fleetrouting.freight.domain.FreightStatus;
import dev.raphaelreis.fleetrouting.risk.infrastructure.outbox.OutboxEventJpaRepository;
import dev.raphaelreis.fleetrouting.risk.infrastructure.persistence.RouteRiskAssessmentJpaRepository;
import dev.raphaelreis.fleetrouting.shared.domain.CellId;
import dev.raphaelreis.fleetrouting.shared.domain.GeoPoint;
import dev.raphaelreis.fleetrouting.telemetry.domain.TelemetryReading;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EvaluateRouteRiskCommandHandlerTest {

    @Autowired EvaluateRouteRiskCommandHandler handler;
    @Autowired RouteRiskAssessmentJpaRepository assessments;
    @Autowired OutboxEventJpaRepository outbox;

    @Test
    void storesAssessmentAndOutboxEventInOneCommandTransaction() {
        var truckId = UUID.randomUUID();
        var freight = new Freight(UUID.randomUUID(), 8200, true, 5.0,
                FreightPriority.CRITICAL, FreightStatus.IN_TRANSIT,
                new GeoPoint(-15.793889, -47.882778), new GeoPoint(-16.686891, -49.264794),
                Instant.parse("2026-08-25T01:00:00Z"), truckId);
        var telemetry = new TelemetryReading(truckId, Instant.parse("2026-08-24T22:30:00Z"),
                new GeoPoint(-16.02, -48.10), 66.6, 11, 8.4, 35);

        var receipt = handler.handle(new EvaluateRouteRiskCommand(new CellId("brs-01"), freight, telemetry));

        assertThat(assessments.existsById(receipt.assessmentId())).isTrue();
        assertThat(outbox.count()).isEqualTo(1);
    }
}
