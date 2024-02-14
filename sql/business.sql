-- 如果表存在，则删除
DROP TABLE IF EXISTS station;

-- 创建表
CREATE TABLE station (
 id BIGINT PRIMARY KEY,
 name VARCHAR(20) NOT NULL,
 name_pinyin VARCHAR(50) NOT NULL,
 name_py VARCHAR(50) NOT NULL,
 create_time TIMESTAMP(0),
 update_time TIMESTAMP(0),
 CONSTRAINT station_name UNIQUE (name)
);

-- 添加注释
COMMENT ON TABLE station IS '车站';
COMMENT ON COLUMN station.id IS 'id';
COMMENT ON COLUMN station.name IS '站名';
COMMENT ON COLUMN station.name_pinyin IS '站名拼音';
COMMENT ON COLUMN station.name_py IS '站名拼音首字母';
COMMENT ON COLUMN station.create_time IS '新增时间';
COMMENT ON COLUMN station.update_time IS '修改时间';

-- 如果表存在，则删除
DROP TABLE IF EXISTS train;

-- 创建表
CREATE TABLE train (
                       id BIGSERIAL PRIMARY KEY,
                       code VARCHAR(20) NOT NULL,
                       type CHAR(1) NOT NULL,
                       start VARCHAR(20) NOT NULL,
                       start_pinyin VARCHAR(50) NOT NULL,
                       start_time TIME NOT NULL,
                       end_val VARCHAR(20) NOT NULL,
                       end_pinyin VARCHAR(50) NOT NULL,
                       end_time TIME NOT NULL,
                       create_time TIMESTAMP(0),
                       update_time TIMESTAMP(0),
                       CONSTRAINT train_code UNIQUE (code)
);

-- 添加注释
COMMENT ON TABLE train IS '车次';
COMMENT ON COLUMN train.id IS 'id';
COMMENT ON COLUMN train.code IS '车次编号';
COMMENT ON COLUMN train.type IS '车次类型|枚举[TrainTypeEnum]';
COMMENT ON COLUMN train.start IS '始发站';
COMMENT ON COLUMN train.start_pinyin IS '始发站拼音';
COMMENT ON COLUMN train.start_time IS '出发时间';
COMMENT ON COLUMN train.end_val IS '终点站';
COMMENT ON COLUMN train.end_pinyin IS '终点站拼音';
COMMENT ON COLUMN train.end_time IS '到站时间';
COMMENT ON COLUMN train.create_time IS '新增时间';
COMMENT ON COLUMN train.update_time IS '修改时间';

-- 如果表存在，则删除
DROP TABLE IF EXISTS train_station;

-- 创建表
CREATE TABLE train_station (
                               id BIGSERIAL PRIMARY KEY,
                               train_code VARCHAR(20) NOT NULL,
                               index INT NOT NULL,
                               name VARCHAR(20) NOT NULL,
                               name_pinyin VARCHAR(50) NOT NULL,
                               in_time TIME,
                               out_time TIME,
                               stop_time TIME,
                               km DECIMAL(8, 2) NOT NULL,
                               create_time TIMESTAMP(0),
                               update_time TIMESTAMP(0),
                               CONSTRAINT unq_train_station_code_index UNIQUE (train_code, index),
                               CONSTRAINT unq_train_station_code_name UNIQUE (train_code, name)
);

-- 添加注释
COMMENT ON TABLE train_station IS '火车车站';
COMMENT ON COLUMN train_station.id IS 'id';
COMMENT ON COLUMN train_station.train_code IS '车次编号';
COMMENT ON COLUMN train_station.index IS '站序';
COMMENT ON COLUMN train_station.name IS '站名';
COMMENT ON COLUMN train_station.name_pinyin IS '站名拼音';
COMMENT ON COLUMN train_station.in_time IS '进站时间';
COMMENT ON COLUMN train_station.out_time IS '出站时间';
COMMENT ON COLUMN train_station.stop_time IS '停站时长';
COMMENT ON COLUMN train_station.km IS '里程（公里）|从上一站到本站的距离';
COMMENT ON COLUMN train_station.create_time IS '新增时间';
COMMENT ON COLUMN train_station.update_time IS '修改时间';

DROP TABLE IF EXISTS train_carriage;
CREATE TABLE train_carriage (
                                id BIGSERIAL PRIMARY KEY,
                                train_code VARCHAR(20) NOT NULL,
                                index INT NOT NULL,
                                seat_type CHAR(1) NOT NULL,
                                seat_count INT NOT NULL,
                                row_count INT NOT NULL,
                                col_count INT NOT NULL,
                                create_time TIMESTAMP(0),
                                update_time TIMESTAMP(0),
                                CONSTRAINT unq_train_carriage_code_num UNIQUE (train_code, index)
);
COMMENT ON TABLE train_carriage IS '火车车厢';
COMMENT ON COLUMN train_carriage.id IS 'id';
COMMENT ON COLUMN train_carriage.train_code IS '车次编号';
COMMENT ON COLUMN train_carriage.index IS '厢号';
COMMENT ON COLUMN train_carriage.seat_type IS '座位类型|枚举[SeatTypeEnum]';
COMMENT ON COLUMN train_carriage.seat_count IS '座位数';
COMMENT ON COLUMN train_carriage.row_count IS '排数';
COMMENT ON COLUMN train_carriage.col_count IS '列数';
COMMENT ON COLUMN train_carriage.create_time IS '新增时间';
COMMENT ON COLUMN train_carriage.update_time IS '修改时间';

-- 如果表存在，则删除
DROP TABLE IF EXISTS train_seat;

-- 创建表
CREATE TABLE train_seat (
                            id BIGSERIAL PRIMARY KEY,
                            train_code VARCHAR(20) NOT NULL,
                            carriage_index INT NOT NULL,
                            row CHAR(2) NOT NULL,
                            col CHAR(1) NOT NULL,
                            seat_type CHAR(1) NOT NULL,
                            carriage_seat_index INT NOT NULL,
                            create_time TIMESTAMP(0),
                            update_time TIMESTAMP(0)
);

-- 添加注释
COMMENT ON TABLE train_seat IS '座位';
COMMENT ON COLUMN train_seat.id IS 'id';
COMMENT ON COLUMN train_seat.train_code IS '车次编号';
COMMENT ON COLUMN train_seat.carriage_index IS '厢序';
COMMENT ON COLUMN train_seat.row IS '排号|01, 02';
COMMENT ON COLUMN train_seat.col IS '列号|枚举[SeatColEnum]';
COMMENT ON COLUMN train_seat.seat_type IS '座位类型|枚举[SeatTypeEnum]';
COMMENT ON COLUMN train_seat.carriage_seat_index IS '同车厢座序';
COMMENT ON COLUMN train_seat.create_time IS '新增时间';
COMMENT ON COLUMN train_seat.update_time IS '修改时间';

-- 如果表存在，则删除
DROP TABLE IF EXISTS daily_train;

-- 创建表
CREATE TABLE daily_train (
                             id BIGINT PRIMARY KEY,
                             date DATE NOT NULL,
                             code VARCHAR(20) NOT NULL,
                             type CHAR(1) NOT NULL,
                             start VARCHAR(20) NOT NULL,
                             start_pinyin VARCHAR(50) NOT NULL,
                             start_time TIME NOT NULL,
                             end_val VARCHAR(20) NOT NULL,
                             end_pinyin VARCHAR(50) NOT NULL,
                             end_time TIME NOT NULL,
                             create_time TIMESTAMP(0),
                             update_time TIMESTAMP(0),
                             CONSTRAINT  unq_daily_train_date_code_unique UNIQUE (date, code)
);

-- 添加注释
COMMENT ON TABLE daily_train IS '每日车次';
COMMENT ON COLUMN daily_train.id IS 'id';
COMMENT ON COLUMN daily_train.date IS '日期';
COMMENT ON COLUMN daily_train.code IS '车次编号';
COMMENT ON COLUMN daily_train.type IS '车次类型|枚举[TrainTypeEnum]';
COMMENT ON COLUMN daily_train.start IS '始发站';
COMMENT ON COLUMN daily_train.start_pinyin IS '始发站拼音';
COMMENT ON COLUMN daily_train.start_time IS '出发时间';
COMMENT ON COLUMN daily_train.end_val IS '终点站';
COMMENT ON COLUMN daily_train.end_pinyin IS '终点站拼音';
COMMENT ON COLUMN daily_train.end_time IS '到站时间';
COMMENT ON COLUMN daily_train.create_time IS '新增时间';
COMMENT ON COLUMN daily_train.update_time IS '修改时间';

-- 如果表存在，则删除
DROP TABLE IF EXISTS daily_train_station;

-- 创建表
CREATE TABLE daily_train_station (
                                     id BIGINT PRIMARY KEY,
                                     date DATE NOT NULL,
                                     train_code VARCHAR(20) NOT NULL,
                                     index INT NOT NULL,
                                     name VARCHAR(20) NOT NULL,
                                     name_pinyin VARCHAR(50) NOT NULL,
                                     in_time TIME,
                                     out_time TIME,
                                     stop_time TIME,
                                     km DECIMAL(8, 2) NOT NULL,
                                     create_time TIMESTAMP(0),
                                     update_time TIMESTAMP(0),
                                     CONSTRAINT unq_daily_train_station_code_index UNIQUE (date, train_code, index),
                                     CONSTRAINT unq_daily_train_station_code_name UNIQUE (date, train_code, name)
);

-- 添加注释
COMMENT ON TABLE daily_train_station IS '每日车站';
COMMENT ON COLUMN daily_train_station.id IS 'id';
COMMENT ON COLUMN daily_train_station.date IS '日期';
COMMENT ON COLUMN daily_train_station.train_code IS '车次编号';
COMMENT ON COLUMN daily_train_station.index IS '站序|第一站是0';
COMMENT ON COLUMN daily_train_station.name IS '站名';
COMMENT ON COLUMN daily_train_station.name_pinyin IS '站名拼音';
COMMENT ON COLUMN daily_train_station.in_time IS '进站时间';
COMMENT ON COLUMN daily_train_station.out_time IS '出站时间';
COMMENT ON COLUMN daily_train_station.stop_time IS '停站时长';
COMMENT ON COLUMN daily_train_station.km IS '里程（公里）|从上一站到本站的距离';
COMMENT ON COLUMN daily_train_station.create_time IS '新增时间';
COMMENT ON COLUMN daily_train_station.update_time IS '修改时间';

-- 如果表存在，则删除
DROP TABLE IF EXISTS daily_train_carriage;

-- 创建表
CREATE TABLE daily_train_carriage (
                                      id BIGINT PRIMARY KEY,
                                      date DATE NOT NULL,
                                      train_code VARCHAR(20) NOT NULL,
                                      index INT NOT NULL,
                                      seat_type CHAR(1) NOT NULL,
                                      seat_count INT NOT NULL,
                                      row_count INT NOT NULL,
                                      col_count INT NOT NULL,
                                      create_time TIMESTAMP,
                                      update_time TIMESTAMP,
                                      CONSTRAINT unq_daily_train_carriage_code_index UNIQUE (date, train_code, index)
);

-- 添加注释
COMMENT ON TABLE daily_train_carriage IS '每日车厢';
COMMENT ON COLUMN daily_train_carriage.id IS 'id';
COMMENT ON COLUMN daily_train_carriage.date IS '日期';
COMMENT ON COLUMN daily_train_carriage.train_code IS '车次编号';
COMMENT ON COLUMN daily_train_carriage.index IS '箱序';
COMMENT ON COLUMN daily_train_carriage.seat_type IS '座位类型|枚举[SeatTypeEnum]';
COMMENT ON COLUMN daily_train_carriage.seat_count IS '座位数';
COMMENT ON COLUMN daily_train_carriage.row_count IS '排数';
COMMENT ON COLUMN daily_train_carriage.col_count IS '列数';
COMMENT ON COLUMN daily_train_carriage.create_time IS '新增时间';
COMMENT ON COLUMN daily_train_carriage.update_time IS '修改时间';

-- 如果表存在，则删除
DROP TABLE IF EXISTS daily_train_seat;

-- 创建表
CREATE TABLE daily_train_seat (
                                  id BIGINT PRIMARY KEY,
                                  date DATE NOT NULL,
                                  train_code VARCHAR(20) NOT NULL,
                                  carriage_index INT NOT NULL,
                                  row CHAR(2) NOT NULL,
                                  col CHAR(1) NOT NULL,
                                  seat_type CHAR(1) NOT NULL,
                                  carriage_seat_index INT NOT NULL,
                                  sell VARCHAR(50) NOT NULL,
                                  create_time TIMESTAMP(0),
                                  update_time TIMESTAMP(0)
);

-- 添加注释
COMMENT ON TABLE daily_train_seat IS '每日座位';
COMMENT ON COLUMN daily_train_seat.id IS 'id';
COMMENT ON COLUMN daily_train_seat.date IS '日期';
COMMENT ON COLUMN daily_train_seat.train_code IS '车次编号';
COMMENT ON COLUMN daily_train_seat.carriage_index IS '箱序';
COMMENT ON COLUMN daily_train_seat.row IS '排号|01, 02';
COMMENT ON COLUMN daily_train_seat.col IS '列号|枚举[SeatColEnum]';
COMMENT ON COLUMN daily_train_seat.seat_type IS '座位类型|枚举[SeatTypeEnum]';
COMMENT ON COLUMN daily_train_seat.carriage_seat_index IS '同车箱座序';
COMMENT ON COLUMN daily_train_seat.sell IS '售卖情况|将经过的车站用01拼接，0表示可卖，1表示已卖';
COMMENT ON COLUMN daily_train_seat.create_time IS '新增时间';
COMMENT ON COLUMN daily_train_seat.update_time IS '修改时间';

DROP TABLE IF EXISTS daily_train_ticket;

-- 创建表
CREATE TABLE daily_train_ticket (
                                    id BIGINT PRIMARY KEY,
                                    date DATE NOT NULL,
                                    train_code VARCHAR(20) NOT NULL,
                                    start VARCHAR(20) NOT NULL,
                                    start_pinyin VARCHAR(50) NOT NULL,
                                    start_time TIME NOT NULL,
                                    start_index INT NOT NULL,
                                    end_val VARCHAR(20) NOT NULL,
                                    end_pinyin VARCHAR(50) NOT NULL,
                                    end_time TIME NOT NULL,
                                    end_index INT NOT NULL,
                                    ydz INT NOT NULL,
                                    ydz_price DECIMAL(8, 2) NOT NULL,
                                    edz INT NOT NULL,
                                    edz_price DECIMAL(8, 2) NOT NULL,
                                    rw INT NOT NULL,
                                    rw_price DECIMAL(8, 2) NOT NULL,
                                    yw INT NOT NULL,
                                    yw_price DECIMAL(8, 2) NOT NULL,
                                    create_time TIMESTAMP(0),
                                    update_time TIMESTAMP(0),
                                    CONSTRAINT unq_daily_train_ticket_date_train_code_start_end UNIQUE (date, train_code, start, end_val)
);

-- 添加注释
COMMENT ON TABLE daily_train_ticket IS '余票信息';
COMMENT ON COLUMN daily_train_ticket.id IS 'id';
COMMENT ON COLUMN daily_train_ticket.date IS '日期';
COMMENT ON COLUMN daily_train_ticket.train_code IS '车次编号';
COMMENT ON COLUMN daily_train_ticket.start IS '出发站';
COMMENT ON COLUMN daily_train_ticket.start_pinyin IS '出发站拼音';
COMMENT ON COLUMN daily_train_ticket.start_time IS '出发时间';
COMMENT ON COLUMN daily_train_ticket.start_index IS '出发站序|本站是整个车次的第几站';
COMMENT ON COLUMN daily_train_ticket.end_val IS '到达站';
COMMENT ON COLUMN daily_train_ticket.end_pinyin IS '到达站拼音';
COMMENT ON COLUMN daily_train_ticket.end_time IS '到站时间';
COMMENT ON COLUMN daily_train_ticket.end_index IS '到站站序|本站是整个车次的第几站';
COMMENT ON COLUMN daily_train_ticket.ydz IS '一等座余票';
COMMENT ON COLUMN daily_train_ticket.ydz_price IS '一等座票价';
COMMENT ON COLUMN daily_train_ticket.edz IS '二等座余票';
COMMENT ON COLUMN daily_train_ticket.edz_price IS '二等座票价';
COMMENT ON COLUMN daily_train_ticket.rw IS '软卧余票';
COMMENT ON COLUMN daily_train_ticket.rw_price IS '软卧票价';
COMMENT ON COLUMN daily_train_ticket.yw IS '硬卧余票';
COMMENT ON COLUMN daily_train_ticket.yw_price IS '硬卧票价';
COMMENT ON COLUMN daily_train_ticket.create_time IS '新增时间';
COMMENT ON COLUMN daily_train_ticket.update_time IS '修改时间';

-- 如果表存在，则删除
DROP TABLE IF EXISTS confirm_order;

-- 创建表
CREATE TABLE confirm_order (
                               id BIGINT PRIMARY KEY,
                               member_id BIGINT NOT NULL,
                               date DATE NOT NULL,
                               train_code VARCHAR(20) NOT NULL,
                               start VARCHAR(20) NOT NULL,
                               end_val VARCHAR(20) NOT NULL,
                               daily_train_ticket_id BIGINT NOT NULL,
                               tickets JSONB NOT NULL,
                               status CHAR(1) NOT NULL,
                               create_time TIMESTAMP(0),
                               update_time TIMESTAMP(0)
);

drop index if exists idx_confirm_order_date_train_code;
CREATE INDEX idx_confirm_order_date_train_code ON confirm_order ("date", train_code);

-- 添加注释
COMMENT ON TABLE confirm_order IS '确认订单';
COMMENT ON COLUMN confirm_order.id IS 'id';
COMMENT ON COLUMN confirm_order.member_id IS '会员id';
COMMENT ON COLUMN confirm_order.date IS '日期';
COMMENT ON COLUMN confirm_order.train_code IS '车次编号';
COMMENT ON COLUMN confirm_order.start IS '出发站';
COMMENT ON COLUMN confirm_order.end_val IS '到达站';
COMMENT ON COLUMN confirm_order.daily_train_ticket_id IS '余票ID';
COMMENT ON COLUMN confirm_order.tickets IS '车票';
COMMENT ON COLUMN confirm_order.status IS '订单状态|枚举[ConfirmOrderStatusEnum]';
COMMENT ON COLUMN confirm_order.create_time IS '新增时间';
COMMENT ON COLUMN confirm_order.update_time IS '修改时间';

DROP TABLE IF EXISTS undo_log;
CREATE TABLE undo_log (
                          id BIGSERIAL PRIMARY KEY,
                          branch_id BIGINT NOT NULL,
                          xid VARCHAR(100) NOT NULL,
                          context VARCHAR(128) NOT NULL,
                          rollback_info BYTEA NOT NULL,
                          log_status INT NOT NULL,
                          log_created TIMESTAMP NOT NULL,
                          log_modified TIMESTAMP NOT NULL,
                          ext VARCHAR(100),
                          CONSTRAINT unq_undo_log UNIQUE (xid, branch_id)
);

-- 如果表存在，则删除
DROP TABLE IF EXISTS sk_token;

-- 创建表
CREATE TABLE sk_token (
                          id BIGSERIAL PRIMARY KEY,
                          date DATE NOT NULL,
                          train_code VARCHAR(20) NOT NULL,
                          count INT NOT NULL,
                          create_time TIMESTAMP(3),
                          update_time TIMESTAMP(3),
                          CONSTRAINT unq_sk_token_date_train_code UNIQUE (date, train_code)
);

-- 添加注释
COMMENT ON TABLE sk_token IS '秒杀令牌';
COMMENT ON COLUMN sk_token.id IS 'id';
COMMENT ON COLUMN sk_token.date IS '日期';
COMMENT ON COLUMN sk_token.train_code IS '车次编号';
COMMENT ON COLUMN sk_token.count IS '令牌余量';
COMMENT ON COLUMN sk_token.create_time IS '新增时间';
COMMENT ON COLUMN sk_token.update_time IS '修改时间';
