package dev.raphaelreis.logisticscopilot.fleet.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TruckTest {

    private final VehicleProfile refrigeratedProfile =
            new VehicleProfile(12_000, 4.2, 2.6, 14, true);

    @Test
    void availableRefrigeratedTruckCanCarryCompatibleFreight() {
        var truck = new Truck(UUID.randomUUID(), "abc1d23", refrigeratedProfile, TruckStatus.AVAILABLE);

        assertThat(truck.licensePlate()).isEqualTo("ABC1D23");
        assertThat(truck.canCarry(8_000, true)).isTrue();
    }

    @Test
    void truckInTransitIsNotAvailableForANewAssignment() {
        var truck = new Truck(UUID.randomUUID(), "ABC1D23", refrigeratedProfile, TruckStatus.IN_TRANSIT);

        assertThat(truck.canCarry(8_000, true)).isFalse();
    }
}

