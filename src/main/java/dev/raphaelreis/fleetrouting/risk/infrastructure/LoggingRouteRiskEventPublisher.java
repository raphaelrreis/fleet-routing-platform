package dev.raphaelreis.fleetrouting.risk.infrastructure;

import dev.raphaelreis.fleetrouting.risk.application.RouteRiskEventPublisher;
import dev.raphaelreis.fleetrouting.risk.application.RouteRiskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!kafka")
public class LoggingRouteRiskEventPublisher implements RouteRiskEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingRouteRiskEventPublisher.class);

    @Override
    public void publish(RouteRiskEvent event) {
        LOGGER.info(
                "event=RouteRiskDetected cellId={} freightId={} truckId={} severity={} reasons={}",
                event.cellId(), event.freightId(), event.truckId(), event.severity(), event.reasons()
        );
    }
}
