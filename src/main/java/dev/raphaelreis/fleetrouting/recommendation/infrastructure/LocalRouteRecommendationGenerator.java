package dev.raphaelreis.fleetrouting.recommendation.infrastructure;

import dev.raphaelreis.fleetrouting.recommendation.application.RouteRecommendation;
import dev.raphaelreis.fleetrouting.recommendation.application.RouteRecommendationGenerator;
import dev.raphaelreis.fleetrouting.risk.application.RouteRiskEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Profile("!azure-ai")
public class LocalRouteRecommendationGenerator implements RouteRecommendationGenerator {

    @Override
    public RouteRecommendation generate(RouteRiskEvent event) {
        var actions = new ArrayList<String>();
        if (event.reasons().contains("CARGO_TEMPERATURE_EXCEEDED")) actions.add("Inspect refrigeration immediately");
        if (event.reasons().contains("LOW_FUEL")) actions.add("Schedule the nearest compatible fuel stop");
        if (event.reasons().contains("DELIVERY_DELAY")) actions.add("Request a deterministic route recalculation");
        if (event.reasons().contains("VEHICLE_STOPPED")) actions.add("Contact dispatch to verify vehicle status");
        return new RouteRecommendation(
                "Hold automatic route changes until dispatch reviews the incident.",
                "The assessment is %s and includes %s.".formatted(event.severity(), String.join(", ", event.reasons())),
                actions
        );
    }
}
