create table player_quest_progress
(
    player_id            int                not null,
    quest_id             int                not null,
    current_step         int                not null,
    completed_objectives TEXT default ''    null,
    finished             bool default false not null,
    constraint player_quest_progress_pk
        primary key (player_id, quest_id)
);

create table account_quest_progress
(
    account_id            int                not null,
    quest_id             int                not null,
    current_step         int                not null,
    completed_objectives TEXT default ''    null,
    finished             bool default false not null,
    constraint account_quest_progress_pk
        primary key (account_id, quest_id)
);
