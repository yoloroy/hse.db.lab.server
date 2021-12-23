create table if not exists tables
(
	id serial
		constraint tables_pk
			primary key,
	minimum_check double precision default 0.0 not null,
	human_capacity integer not null
);

alter table tables owner to urijlaruskin;

grant select, update, usage on sequence tables_id_seq to "RestaurantDBServer";

create unique index if not exists tables_id_uindex
	on tables (id);

grant delete, insert, references, select, trigger, truncate, update on tables to "RestaurantDBServer";

grant delete, insert, references, select, trigger, truncate, update on tables to restaurantdbserver;

create table if not exists clients
(
	id serial
		constraint clients_pk
			primary key,
	name text not null,
	discount double precision default 0.0 not null,
	ban boolean default false not null
);

alter table clients owner to urijlaruskin;

grant select, update, usage on sequence clients_id_seq to "RestaurantDBServer";

create unique index if not exists clients_id_uindex
	on clients (id);

grant delete, insert, references, select, trigger, truncate, update on clients to "RestaurantDBServer";

grant delete, insert, references, select, trigger, truncate, update on clients to restaurantdbserver;

create table if not exists bookings_tables
(
	booking_id integer not null
		constraint bookings_tables_pkey
			primary key,
	table_id integer not null
		constraint bookings_tables_tables_id_fk
			references tables
				on delete restrict
);

alter table bookings_tables owner to urijlaruskin;

create unique index if not exists bookings_tables_id_uindex
	on bookings_tables (booking_id);

grant insert, select, update on bookings_tables to "RestaurantDBServer";

create table if not exists booking_timeranges
(
	booking_id integer not null
		constraint booking_tables_id___fk
			references bookings_tables,
	start_time timestamp not null,
	end_time timestamp not null
);

alter table booking_timeranges owner to urijlaruskin;

create index if not exists booking_timeranges_start_time_end_time_index
	on booking_timeranges (start_time desc, end_time desc);

grant insert, select, update on booking_timeranges to "RestaurantDBServer";

create table if not exists bookings_clients
(
	booking_id serial
		constraint bookings_clients_pk
			primary key,
	client_id integer not null
		constraint client___fk
			references clients
);

alter table bookings_clients owner to urijlaruskin;

grant select, update, usage on sequence bookings_clients_booking_id_seq to "RestaurantDBServer";

grant insert, select, update on bookings_clients to "RestaurantDBServer";

create table if not exists deleted_tables
(
	id integer not null
		constraint deleted_tables_tables_id_fk
			references tables
);

alter table deleted_tables owner to urijlaruskin;

grant insert, select on deleted_tables to "RestaurantDBServer";

create or replace view bookings_(booking_id, client_id, table_id, start_time, end_time) as
	SELECT bc.booking_id,
       bc.client_id,
       bt.table_id,
       btr.start_time,
       btr.end_time
FROM bookings_clients bc
         JOIN bookings_tables bt ON bc.booking_id = bt.booking_id
         JOIN booking_timeranges btr ON bt.booking_id = btr.booking_id;

alter table bookings_ owner to urijlaruskin;

grant select on bookings_ to "RestaurantDBServer";

create or replace view tables_status_(booking_id, time, value) as
	SELECT booking_timeranges.booking_id,
       booking_timeranges.start_time AS "time",
       + 1                           AS value
FROM booking_timeranges
UNION ALL
SELECT booking_timeranges.booking_id,
       booking_timeranges.end_time AS "time",
       '-1'::integer               AS value
FROM booking_timeranges;

alter table tables_status_ owner to urijlaruskin;

grant select on tables_status_ to "RestaurantDBServer";

create or replace view bookings_clients_tables(booking_id, client_id, table_id) as
	SELECT bc.booking_id,
       bc.client_id,
       bt.table_id
FROM bookings_clients bc
         JOIN bookings_tables bt ON bc.booking_id = bt.booking_id;

alter table bookings_clients_tables owner to urijlaruskin;

create or replace function client_add(_name text, _discount double precision, _ban boolean) returns clients
	strict
	language sql
as $$
insert into clients (name, discount, ban) values (_name, _discount, _ban) returning id, name, discount, ban;
$$;

alter function client_add(text, double precision, boolean) owner to "RestaurantDBServer";

create or replace function client_ban_set(_id integer, _ban boolean) returns boolean
	strict
	language sql
as $$
with updated as (
    update clients set ban=_ban where id=_id returning id
)
select count(*)>0 as result from updated;
$$;

alter function client_ban_set(integer, boolean) owner to "RestaurantDBServer";

create or replace function client_discount_set(_id integer, _discount double precision) returns boolean
	strict
	language sql
as $$
with updated as (
    update clients set discount=_discount where id=_id returning id
)
select count(*)>0 as result from updated;
$$;

alter function client_discount_set(integer, double precision) owner to "RestaurantDBServer";

create or replace function booking_add(_client_id integer, _table_id integer, _start_time timestamp without time zone, _end_time timestamp without time zone) returns TABLE(booking_id integer, client_id integer, table_id integer, start_time timestamp without time zone, end_time timestamp without time zone)
	strict
	language plpgsql
as $$
declare
    new_id integer;
begin
    insert into bookings_clients as bc (client_id) values (_client_id) returning bc.booking_id into new_id;
    insert into bookings_tables values (new_id, _table_id);
    insert into booking_timeranges values (new_id, _start_time, _end_time);

    return query (select * from bookings_ b where b.booking_id = new_id);
end
$$;

alter function booking_add(integer, integer, timestamp, timestamp) owner to "RestaurantDBServer";

create or replace function update_clients_discount_per_booking_p() returns trigger
	language plpgsql
as $$
begin
    update clients
    set discount = discounts.value
    from (
             select 0.5 * count(*) / 100 value, bc.client_id as client_id
             from bookings_clients bc
                      join booking_timeranges bt on bc.booking_id = bt.booking_id
             where bt.end_time >= ('now'::timestamp - '1 month'::interval)
             group by bc.client_id
         ) discounts
    where new.client_id = discounts.client_id;
    return null;
end
$$;

alter function update_clients_discount_per_booking_p() owner to urijlaruskin;

create trigger update_clients_discount_per_booking
	after insert
	on bookings_clients
	for each row
	execute procedure update_clients_discount_per_booking_p();

comment on trigger update_clients_discount_per_booking on bookings_clients is 'adds +0.5% discount per each booking for the past month';

create or replace function table_update(_id integer, _minimum_check double precision, _human_capacity integer) returns TABLE(id integer, minimum_check double precision, human_capacity integer)
	language plpgsql
as $$
begin
    return query
        update tables
        set minimum_check = _minimum_check, human_capacity = _human_capacity
        where id = _id
        returning id, minimum_check, human_capacity;
end
$$;

alter function table_update(integer, double precision, integer) owner to urijlaruskin;

grant execute on function table_update(integer, double precision, integer) to "RestaurantDBServer";

create or replace function tables_get_monthly_usage_stats() returns TABLE(table_id integer, usage_count bigint, mnth integer)
	language plpgsql
as $$
begin
    return query
        with table_month as (
            select t.id table_id, mnths.month_number mnth
            from tables t
                     cross join (values (1), (2), (3), (4), (5), (6), (7), (8), (9), (10), (11), (12)) mnths (month_number)
        )
        select tm.table_id table_id, sum(coalesce(btr.value, 0)) usage_count, tm.mnth mnth
        from table_month tm
                 join bookings_tables bt on bt.table_id = tm.table_id
                 full outer join (
            select *, 1 value from booking_timeranges
        ) btr
            on
                btr.booking_id = bt.booking_id
            and
                tm.mnth between date_part('month', start_time) and date_part('month', end_time)
        group by tm.mnth, tm.table_id
        order by table_id, mnth;
end;
$$;

alter function tables_get_monthly_usage_stats() owner to urijlaruskin;

create or replace function tables_get_average_usage_per_hour() returns TABLE(table_id integer, hour integer, average_usage double precision)
	language plpgsql
as $$
begin
    return query
        with tables_hours as (
            select t.id as table_id, hours.hour as hour
            from tables t
            cross join
                 (values (1), (2), (3), (4), (5), (6), (7), (8), (9), (10), (11), (12), (13), (14), (15), (16), (17), (18), (19), (20), (21), (22), (23), (24)) hours (hour)
        )
        select t.table_id as table_id, t.hour as hour, avg(coalesce(btr.value, 0))::double precision average_usage
        from tables_hours t
        join bookings_tables bt on bt.table_id = t.table_id
        full outer join (
            select *, 1 value
            from booking_timeranges
            group by booking_id, start_time, end_time
        ) btr on
            btr.booking_id = bt.booking_id
                and
            t.hour between date_part('hour', start_time) and date_part('hour', end_time)
        group by t.table_id, t.hour
        order by t.table_id, t.hour;
end;
$$;

alter function tables_get_average_usage_per_hour() owner to urijlaruskin;

create or replace function table_delete(_id integer) returns TABLE(id integer, minimum_check double precision, human_capacity integer)
	language plpgsql
as $$
begin
    insert into deleted_tables (id) values (_id);
    return query select * from tables where tables.id = _id;
end
$$;

alter function table_delete(integer) owner to urijlaruskin;

create or replace function table_get_all() returns TABLE(id integer, minimum_check double precision, human_capacity integer)
	language plpgsql
as $$
begin
    return query
        select tables.id, tables.minimum_check, tables.human_capacity
        from tables
        left join deleted_tables dt on tables.id = dt.id
        where dt.id is null;
end;
$$;

alter function table_get_all() owner to urijlaruskin;

create or replace function table_add(_minimum_check double precision, _capacity integer) returns TABLE(id integer, minimum_check double precision, human_capacity integer)
	language plpgsql
as $$
begin
    return query
        insert into tables (minimum_check, human_capacity)
        values (_minimum_check, _capacity)
        returning tables.id, tables.minimum_check, tables.human_capacity;
end;
$$;

alter function table_add(double precision, integer) owner to urijlaruskin;

create or replace function tables_status_get_all_for_interval(interval_start timestamp without time zone, interval_end timestamp without time zone) returns TABLE(table_id integer, is_booked boolean, booking_id integer)
	language plpgsql
as $$
begin
    return query
        select t.table_id, t.is_booked, t.booking_id
        from (
            with
                before_start_sums as (
                    select bt.table_id, sum(value) != 0 as value
                    from tables_status_
                    inner join bookings_tables bt on tables_status_.booking_id = bt.booking_id
                    where time <= interval_start
                    group by bt.table_id
                ),
                on_interval_usages as (
                    select bt.table_id, true as value
                    from tables_status_
                    inner join bookings_tables bt on tables_status_.booking_id = bt.booking_id
                    where time between interval_start and interval_end
                ),
                last_usages as (
                    select bt.table_id, last_value(bt.booking_id) over (order by btr.end_time) as booking_id
                    from bookings_tables bt
                             inner join booking_timeranges btr on bt.booking_id = btr.booking_id
                )
            select
                tables.id                                                     as table_id,
                coalesce(sms.value, false) or coalesce(usages.value, false)   as is_booked,
                case when is_booked then lu.booking_id end                    as booking_id,
                row_number() over (partition by tables.id order by tables.id) as row_id
            from table_get_all() tables
                     left join before_start_sums sms on sms.table_id = tables.id
                     left join on_interval_usages usages on usages.table_id = tables.id
                     left join last_usages lu on lu.table_id = tables.id
        ) t
        where t.row_id = 1;
end
$$;

alter function tables_status_get_all_for_interval(timestamp, timestamp) owner to urijlaruskin;

