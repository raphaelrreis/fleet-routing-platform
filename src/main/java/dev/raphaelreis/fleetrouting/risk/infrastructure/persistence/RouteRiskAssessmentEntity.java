package dev.raphaelreis.fleetrouting.risk.infrastructure.persistence;

import dev.raphaelreis.fleetrouting.risk.domain.RouteRiskAssessment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "route_risk_assessment")
public class RouteRiskAssessmentEntity {

    @Id
    private UUID id;
    private String cellId;
    private UUID freightId;
    private UUID truckId;
    private Instant assessedAt;
    private String severity;
    @Column(length = 512)
    private String reasons;
    private boolean atRisk;
    private String recommendationStatus;
    @Column(length = 2000)
    private String recommendation;
    @Column(length = 4000)
    private String rationale;
    @Column(length = 4000)
    private String requiredActions;

    protected RouteRiskAssessmentEntity() {
    }

    public RouteRiskAssessmentEntity(RouteRiskAssessment assessment) {
        this.id = assessment.assessmentId();
        this.cellId = assessment.cellId().value();
        this.freightId = assessment.freightId();
        this.truckId = assessment.truckId();
        this.assessedAt = assessment.assessedAt();
        this.severity = assessment.severity().name();
        this.reasons = assessment.reasons().stream().map(Enum::name).sorted().collect(Collectors.joining(","));
        this.atRisk = assessment.atRisk();
        this.recommendationStatus = RecommendationStatus.PENDING.name();
    }

    public void completeRecommendation(String recommendation, String rationale, String requiredActions) {
        this.recommendation = recommendation;
        this.rationale = rationale;
        this.requiredActions = requiredActions;
        this.recommendationStatus = RecommendationStatus.COMPLETED.name();
    }

    public void failRecommendation() {
        this.recommendationStatus = RecommendationStatus.FAILED.name();
    }

    public UUID getId() { return id; }
    public String getCellId() { return cellId; }
    public UUID getFreightId() { return freightId; }
    public UUID getTruckId() { return truckId; }
    public Instant getAssessedAt() { return assessedAt; }
    public String getSeverity() { return severity; }
    public String getReasons() { return reasons; }
    public boolean isAtRisk() { return atRisk; }
    public String getRecommendationStatus() { return recommendationStatus; }
    public String getRecommendation() { return recommendation; }
    public String getRationale() { return rationale; }
    public String getRequiredActions() { return requiredActions; }
}
