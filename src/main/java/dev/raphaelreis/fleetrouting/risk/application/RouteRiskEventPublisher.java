package dev.raphaelreis.fleetrouting.risk.application;

public interface RouteRiskEventPublisher {

    void publish(RouteRiskEvent event);
}
