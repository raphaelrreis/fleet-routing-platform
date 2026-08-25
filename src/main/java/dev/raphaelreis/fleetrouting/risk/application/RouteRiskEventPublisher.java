package dev.raphaelreis.fleetrouting.risk.application;

import dev.raphaelreis.fleetrouting.risk.domain.RouteRiskAssessment;

public interface RouteRiskEventPublisher {

    void publish(RouteRiskAssessment assessment);
}

