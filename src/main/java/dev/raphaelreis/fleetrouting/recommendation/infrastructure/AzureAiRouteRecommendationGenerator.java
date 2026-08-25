package dev.raphaelreis.fleetrouting.recommendation.infrastructure;

import dev.raphaelreis.fleetrouting.recommendation.application.RouteRecommendation;
import dev.raphaelreis.fleetrouting.recommendation.application.RouteRecommendationGenerator;
import dev.raphaelreis.fleetrouting.risk.application.RouteRiskEvent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("azure-ai")
public class AzureAiRouteRecommendationGenerator implements RouteRecommendationGenerator {

    private final ChatClient chatClient;

    public AzureAiRouteRecommendationGenerator(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public RouteRecommendation generate(RouteRiskEvent event) {
        return chatClient.prompt()
                .system("""
                        You support a fleet dispatcher. Use only the supplied assessment facts.
                        Do not calculate or invent routes, distances, ETAs, coordinates, or vehicle availability.
                        Recommend safe next actions that require dispatch approval.
                        """)
                .user("Assessment: %s".formatted(event))
                .call()
                .entity(RouteRecommendation.class, spec -> spec.validateSchema());
    }
}
