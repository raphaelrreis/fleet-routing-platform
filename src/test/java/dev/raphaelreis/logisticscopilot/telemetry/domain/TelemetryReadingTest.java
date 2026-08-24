package dev.raphaelreis.logisticscopilot.telemetry.domain;

import dev.raphaelreis.logisticscopilot.shared.domain.GeoPoint;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class TelemetryReadingTest {

    @Test
    void rejectsFuelPercentageOutsideThePhysicalRange() {
        assertThatIllegalArgumentException().isThrownBy(() -> new TelemetryReading(
                UUID.randomUUID(),
                Instant.now(),
                new GeoPoint(-15.793889, -47.882778),
                70,
                101,
                4.0,
                0
        ));
    }
}

