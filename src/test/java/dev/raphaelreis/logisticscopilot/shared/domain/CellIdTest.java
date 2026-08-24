package dev.raphaelreis.logisticscopilot.shared.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CellIdTest {

    @Test
    void acceptsStableLowercaseCellIdentifier() {
        assertThat(new CellId("brs-01").value()).isEqualTo("brs-01");
    }

    @Test
    void rejectsIdentifiersThatCannotBeUsedForRoutingAndTags() {
        assertThatIllegalArgumentException().isThrownBy(() -> new CellId("BRS 01"));
    }
}

