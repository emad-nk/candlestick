create table if not exists quote
(
    isin                varchar(100)               not null,
    timestamp           timestamp                  not null,
    price               double precision           not null,

    constraint quote_fkey
        foreign key (isin)
        references instrument(isin) on delete cascade,
    primary key (isin, timestamp)
);

create index if not exists timestamp_idx on quote (timestamp);
