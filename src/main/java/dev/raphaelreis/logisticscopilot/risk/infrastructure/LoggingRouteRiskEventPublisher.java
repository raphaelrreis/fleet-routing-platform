package dev.raphaelreis.logisticscopilot.risk.infrastructure;

import dev.raphaelreis.logisticscopilot.risk.application.RouteRiskEventPublisher;
import dev.raphaelreis.logisticscopilot.risk.domain.RouteRiskAssessment;
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
                "event=RouteRiskDetected freightId={} truckId={} severity={} reasons={}",
                assessment.freightId(),
                assessment.truckId(),
                assessment.severity(),
                assessment.reasons()
        );
    }
}
