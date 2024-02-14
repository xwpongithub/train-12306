drop table if exists public.member;
create table public.member (
   id bigint,
   mobile varchar(11) not null,
   primary key (id),
   CONSTRAINT unq_member_mobile UNIQUE (mobile)
) WITH (OIDS = false) TABLESPACE pg_default;

COMMENT ON TABLE public.member IS '会员账号表';
COMMENT ON COLUMN public.member.mobile IS '手机号';

drop table if exists public.passenger;
create table public.passenger (
                                  id bigint,
                                  member_id bigint not null,
                                  name varchar(16) not null,
                                  id_card varchar(18) not null,
                                  type char(1) not null,
                                  create_time timestamp(0),
                                  update_time timestamp(0),
                                  primary key (id)
) WITH (OIDS = false) TABLESPACE pg_default;

CREATE INDEX passenger_member_id ON public.passenger(member_id);
COMMENT ON TABLE public.passenger IS '乘车人';
COMMENT ON COLUMN public.passenger.member_id IS '添加此乘车人的会员账号id';
COMMENT ON COLUMN public.passenger.name IS '姓名';
COMMENT ON COLUMN public.passenger.id_card IS '身份证';
COMMENT ON COLUMN public.passenger.type IS '旅客类型|枚举[PassengerTypeEnum]';
COMMENT ON COLUMN public.passenger.create_time IS '新增时间';
COMMENT ON COLUMN public.passenger.update_time IS '修改时间';


drop table if exists ticket;
create table ticket (
                        id bigint not null,
                        member_id bigint not null,
                        passenger_id bigint not null,
                        passenger_name varchar(20),
                        train_date date not null,
                        train_code varchar(20) not null,
                        carriage_index int not null,
                        seat_row char(2) not null,
                        seat_col char(1) not null,
                        start_station varchar(20) not null,
                        start_time time not null,
                        end_station varchar(20) not null,
                        end_time time not null,
                        seat_type char(1) not null ,
                        create_time timestamp(3),
                        update_time timestamp(3),
                        primary key (id)
);
comment on table ticket is '车票';
comment on column ticket.id is 'id';
comment on column ticket.member_id is '会员id';
comment on column ticket.passenger_id is '乘客id';
comment on column ticket.passenger_name is '乘客姓名';
comment on column ticket.train_date is '日期';
comment on column ticket.train_code is '车次编号';
comment on column ticket.carriage_index is '箱序';
comment on column ticket.seat_row is '排号|01, 02';
comment on column ticket.seat_col is '列号|枚举[SeatColEnum]';
comment on column ticket.start_station is '出发站';
comment on column ticket.start_time is '出发时间';
comment on column ticket.end_station is '到达站';
comment on column ticket.end_time is '到站时间';
comment on column ticket.seat_type is '座位类型|枚举[SeatTypeEnum]';
comment on column ticket.create_time is '新增时间';
comment on column ticket.update_time is '修改时间';

CREATE INDEX ticket_member_id ON public.ticket(member_id);
