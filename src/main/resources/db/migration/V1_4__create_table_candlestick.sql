create table if not exists candlestick
(
    isin                varchar(100)               not null,
    open_timestamp      timestamp                  not null,
    close_timestamp     timestamp                  not null,
    open_price          double precision           not null,
    high_price          double precision           not null,
    low_price           double precision           not null,
    closing_price       double precision           not null,
    constraint candlestick_fkey
        foreign key (isin)
        references instrument(isin) on delete cascade,
    primary key (isin, open_timestamp)
);

create index if not exists open_timestamp_idx on candlestick (open_timestamp);
