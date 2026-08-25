package dev.raphaelreis.fleetrouting.risk.infrastructure;

import dev.raphaelreis.fleetrouting.risk.application.RouteRiskEventPublisher;
import dev.raphaelreis.fleetrouting.risk.domain.RouteRiskAssessment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!azure")
public class LoggingRouteRiskEventPublisher implements RouteRiskEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingRouteRiskEventPublisher.class);

    @Override
    public void publish(RouteRiskAssessment assessment) {
        LOGGER.info(
                "event=RouteRiskDetected cellId={} freightId={} truckId={} severity={} reasons={}",
                assessment.cellId(),
                assessment.freightId(),
                assessment.truckId(),
                assessment.severity(),
                assessment.reasons()
        );
    }
}
