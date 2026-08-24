package dev.raphaelreis.logisticscopilot.risk.application;

import dev.raphaelreis.logisticscopilot.risk.domain.RouteRiskAssessment;

public interface RouteRiskEventPublisher {

    void publish(RouteRiskAssessment assessment);
}

