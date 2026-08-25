package dev.raphaelreis.fleetrouting.risk.api;

import dev.raphaelreis.fleetrouting.freight.domain.Freight;
import dev.raphaelreis.fleetrouting.freight.domain.FreightPriority;
import dev.raphaelreis.fleetrouting.freight.domain.FreightStatus;
import dev.raphaelreis.fleetrouting.risk.application.EvaluateRouteRiskCommand;
import dev.raphaelreis.fleetrouting.risk.application.EvaluateRouteRiskCommandHandler;
import dev.raphaelreis.fleetrouting.risk.application.RouteRiskAssessmentReceipt;
import dev.raphaelreis.fleetrouting.risk.query.GetRouteRiskAssessmentQuery;
import dev.raphaelreis.fleetrouting.risk.query.RouteRiskAssessmentQueryHandler;
import dev.raphaelreis.fleetrouting.risk.query.RouteRiskAssessmentView;
import dev.raphaelreis.fleetrouting.shared.domain.CellId;
import dev.raphaelreis.fleetrouting.shared.domain.GeoPoint;
import dev.raphaelreis.fleetrouting.telemetry.infrastructure.acl.TelematicsPayload;
import dev.raphaelreis.fleetrouting.telemetry.infrastructure.acl.TelemetryAntiCorruptionLayer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/route-risk-assessments")
public class RouteRiskAssessmentController {

    private final EvaluateRouteRiskCommandHandler commandHandler;
    private final RouteRiskAssessmentQueryHandler queryHandler;
    private final TelemetryAntiCorruptionLayer telemetryAcl;

    public RouteRiskAssessmentController(
            EvaluateRouteRiskCommandHandler commandHandler,
            RouteRiskAssessmentQueryHandler queryHandler,
            TelemetryAntiCorruptionLayer telemetryAcl
    ) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
        this.telemetryAcl = telemetryAcl;
    }

    @PostMapping
    public ResponseEntity<RouteRiskAssessmentReceipt> evaluate(@Valid @RequestBody AssessmentRequest request) {
        var freight = request.shipment().toDomain();
        var telemetry = telemetryAcl.toDomain(freight.assignedTruckId(), request.telemetry().toPayload());
        var receipt = commandHandler.handle(new EvaluateRouteRiskCommand(new CellId(request.cellId()), freight, telemetry));
        return ResponseEntity.created(URI.create("/api/v1/route-risk-assessments/" + receipt.assessmentId())).body(receipt);
    }

    @GetMapping("/{assessmentId}")
    public ResponseEntity<RouteRiskAssessmentView> get(@PathVariable UUID assessmentId) {
        return queryHandler.handle(new GetRouteRiskAssessmentQuery(assessmentId))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record AssessmentRequest(
            @NotBlank String cellId,
            @Valid @NotNull ShipmentRequest shipment,
            @Valid @NotNull TelemetryRequest telemetry
    ) {
    }

    public record ShipmentRequest(
            @NotNull UUID shipmentId,
            @Positive double weightKg,
            boolean requiresRefrigeration,
            Double maximumCargoTemperatureCelsius,
            @NotNull FreightPriority priority,
            @NotNull FreightStatus status,
            @Valid @NotNull PositionRequest origin,
            @Valid @NotNull PositionRequest destination,
            @NotNull Instant deliveryDeadline,
            @NotNull UUID assignedTruckId
    ) {
        Freight toDomain() {
            return new Freight(shipmentId, weightKg, requiresRefrigeration, maximumCargoTemperatureCelsius,
                    priority, status, origin.toDomain(), destination.toDomain(), deliveryDeadline, assignedTruckId);
        }
    }

    public record PositionRequest(
            @DecimalMin("-90") @DecimalMax("90") double latitude,
            @DecimalMin("-180") @DecimalMax("180") double longitude
    ) {
        GeoPoint toDomain() { return new GeoPoint(latitude, longitude); }
    }

    public record TelemetryRequest(
            @NotNull Instant observedAt,
            @DecimalMin("-90") @DecimalMax("90") double latitude,
            @DecimalMin("-180") @DecimalMax("180") double longitude,
            @PositiveOrZero double speedMetersPerSecond,
            @DecimalMin("0") @DecimalMax("1") double fuelLevelRatio,
            Double cargoTemperatureCelsius,
            @PositiveOrZero int estimatedDelaySeconds
    ) {
        TelematicsPayload toPayload() {
            return new TelematicsPayload(observedAt, latitude, longitude, speedMetersPerSecond,
                    fuelLevelRatio, cargoTemperatureCelsius, estimatedDelaySeconds);
        }
    }
}
