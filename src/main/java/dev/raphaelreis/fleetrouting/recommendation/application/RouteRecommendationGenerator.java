package dev.raphaelreis.fleetrouting.recommendation.application;

import dev.raphaelreis.fleetrouting.risk.application.RouteRiskEvent;

public interface RouteRecommendationGenerator {

    RouteRecommendation generate(RouteRiskEvent event);
}
