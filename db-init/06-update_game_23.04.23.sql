USE starloco_game;

create table quest_progress
(
    account_id           int                   not null,
    player_id            int                   not null,
    quest_id             int                   not null,
    current_step         int                   not null,
    completed_objectives text       default '' not null,
    finished             tinyint(1) default 0  not null,
    primary key (account_id, player_id, quest_id)
);

