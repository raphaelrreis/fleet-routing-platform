package dev.raphaelreis.fleetrouting.risk.infrastructure;

import dev.raphaelreis.fleetrouting.risk.application.RouteRiskEvent;
import dev.raphaelreis.fleetrouting.risk.application.RouteRiskEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class KafkaRouteRiskEventPublisher implements RouteRiskEventPublisher {

    private final KafkaTemplate<String, RouteRiskEvent> kafkaTemplate;

    public KafkaRouteRiskEventPublisher(KafkaTemplate<String, RouteRiskEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(RouteRiskEvent event) {
        kafkaTemplate.send("route-risk-detected.v1", event.cellId(), event).join();
    }
}
