package dev.raphaelreis.fleetrouting.risk.query;

import dev.raphaelreis.fleetrouting.risk.infrastructure.persistence.RouteRiskAssessmentEntity;
import dev.raphaelreis.fleetrouting.risk.infrastructure.persistence.RouteRiskAssessmentJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class RouteRiskAssessmentQueryHandler {

    private final RouteRiskAssessmentJpaRepository repository;

    public RouteRiskAssessmentQueryHandler(RouteRiskAssessmentJpaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<RouteRiskAssessmentView> handle(GetRouteRiskAssessmentQuery query) {
        return repository.findById(query.assessmentId()).map(this::toView);
    }

    private RouteRiskAssessmentView toView(RouteRiskAssessmentEntity entity) {
        return new RouteRiskAssessmentView(
                entity.getId(), entity.getCellId(), entity.getFreightId(), entity.getTruckId(),
                entity.getAssessedAt(), entity.getSeverity(), split(entity.getReasons()), entity.isAtRisk(),
                entity.getRecommendationStatus(), entity.getRecommendation(), entity.getRationale(),
                split(entity.getRequiredActions())
        );
    }

    private static List<String> split(String value) {
        return value == null || value.isBlank() ? List.of() : Arrays.asList(value.split(","));
    }
}
