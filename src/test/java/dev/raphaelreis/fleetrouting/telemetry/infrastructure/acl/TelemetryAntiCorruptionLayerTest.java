package dev.raphaelreis.fleetrouting.telemetry.infrastructure.acl;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TelemetryAntiCorruptionLayerTest {

    @Test
    void convertsExternalUnitsIntoCanonicalDomainUnits() {
        var payload = new TelematicsPayload(
                Instant.parse("2026-08-24T18:00:00Z"),
                -16.0200, -48.1000, 20, 0.42, 4.8, 125
        );

        var reading = new TelemetryAntiCorruptionLayer().toDomain(UUID.randomUUID(), payload);

        assertThat(reading.speedKph()).isEqualTo(72);
        assertThat(reading.fuelPercentage()).isEqualTo(42);
        assertThat(reading.estimatedDelayMinutes()).isEqualTo(3);
    }
}
