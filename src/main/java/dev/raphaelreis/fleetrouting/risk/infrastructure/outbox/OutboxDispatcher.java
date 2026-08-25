package dev.raphaelreis.fleetrouting.risk.infrastructure.outbox;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class OutboxDispatcher {

    private final OutboxEventJpaRepository repository;
    private final OutboxDispatchService dispatchService;
    private final Clock clock = Clock.systemUTC();

    public OutboxDispatcher(OutboxEventJpaRepository repository, OutboxDispatchService dispatchService) {
        this.repository = repository;
        this.dispatchService = dispatchService;
    }

    @Scheduled(fixedDelayString = "${fleet-routing.outbox.dispatch-delay}")
    public void dispatchPendingEvents() {
        repository.findTop50ByStatusAndNextAttemptAtLessThanEqualOrderByOccurredAt(
                OutboxStatus.PENDING.name(), clock.instant()
        ).forEach(event -> dispatchService.dispatch(event.getId()));
    }
}
