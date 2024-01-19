create table if not exists instrument
(
    isin                varchar(100)    not null constraint instrument_pk primary key,
    scheduled_to        timestamp       not null,
    description         text            not null
);
