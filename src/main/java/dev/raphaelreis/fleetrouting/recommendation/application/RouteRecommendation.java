package dev.raphaelreis.fleetrouting.recommendation.application;

import java.util.List;

public record RouteRecommendation(String recommendation, String rationale, List<String> requiredActions) {
}
