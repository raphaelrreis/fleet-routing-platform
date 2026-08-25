create table if not exists route_risk_assessment (
    id uuid primary key,
    cell_id varchar(32) not null,
    freight_id uuid not null,
    truck_id uuid not null,
    assessed_at timestamp with time zone not null,
    severity varchar(16) not null,
    reasons varchar(512) not null,
    at_risk boolean not null,
    recommendation_status varchar(16) not null,
    recommendation varchar(2000),
    rationale varchar(4000),
    required_actions varchar(4000)
);

create index if not exists idx_route_risk_freight on route_risk_assessment (freight_id, assessed_at);

create table if not exists outbox_event (
    id uuid primary key,
    aggregate_id uuid not null,
    event_type varchar(128) not null,
    occurred_at timestamp with time zone not null,
    status varchar(16) not null,
    attempts integer not null,
    next_attempt_at timestamp with time zone not null,
    last_error varchar(2000),
    version bigint not null
);

create index if not exists idx_outbox_dispatch on outbox_event (status, next_attempt_at, occurred_at);
