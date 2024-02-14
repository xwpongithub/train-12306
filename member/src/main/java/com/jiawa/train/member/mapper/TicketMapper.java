package com.jiawa.train.member.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import com.jiawa.train.member.entity.Passenger;
import com.jiawa.train.member.entity.Ticket;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Objects;

@Repository
public class TicketMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "ticket";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_MEMBER_ID = "member_id";

    private static final String FIELD_PASSENGER_ID = "passenger_id";

    private static final String FIELD_PASSENGER_NAME = "passenger_name";

    private static final String FIELD_TRAIN_DATE ="train_date";

    private static final String FIELD_TRAIN_CODE="train_code";

    private static final String FIELD_CARRIAGE_INDEX="carriage_index";

    private static final String FIELD_SEAT_ROW="seat_row";

    private static final String FIELD_SEAT_COL = "seat_col";

    private static final String FIELD_START_STATION = "start_station";

    private static final String FIELD_START_TIME = "start_time";

    private static final String FIELD_END_STATION = "end_station";

    private static final String FIELD_END_TIME ="end_time";

    private static final String FIELD_SEAT_TYPE = "seat_type";

    private static final String FIELD_CREATE_TIME = "create_time";

    private static final String FIELD_UPDATE_TIME = "update_time";

    private static final String[] FIELDS = {PRIMARY_KEY,
            FIELD_MEMBER_ID,
            FIELD_PASSENGER_ID,
            FIELD_PASSENGER_NAME,
            FIELD_TRAIN_DATE,
            FIELD_TRAIN_CODE,
            FIELD_CARRIAGE_INDEX,
        FIELD_SEAT_ROW,
        FIELD_SEAT_COL,
        FIELD_START_STATION,
        FIELD_START_TIME,
        FIELD_END_STATION,
        FIELD_END_TIME,
        FIELD_SEAT_TYPE,
        FIELD_CREATE_TIME,
        FIELD_UPDATE_TIME
};

    private static String getSelectSql() {
        return ArrayUtil.join(FIELDS,",");
    }

    public long insert(Ticket ticket) {
        return insert(ticket, false);
    }

    public long insertSelective(Ticket ticket) {
        return insert(ticket,true);
    }

    public PageResult<Ticket> page(int page, int size, Long memberId) {
        try{
            PageResult<Entity> dbRs;
            if (Objects.nonNull(memberId)) {
              dbRs =Db.use().page("select " + getSelectSql() + " from " + TABLE + " where member_id = ? order by id asc", new Page(page - 1, size), memberId);
            } else {
                dbRs =Db.use().page("select " + getSelectSql() + " from " + TABLE + " order by id desc", new Page(page - 1, size));
            }
            var pageRs = new PageResult<Ticket>();
            for (var rs : dbRs) {
                Ticket ticket = new Ticket(
                        rs.getLong(PRIMARY_KEY),
                        rs.getLong(FIELD_MEMBER_ID),
                        rs.getLong(FIELD_PASSENGER_ID),
                        rs.getStr(FIELD_PASSENGER_NAME),
                        rs.getDate(FIELD_TRAIN_DATE),
                        rs.getStr(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_CARRIAGE_INDEX),
                        rs.getStr(FIELD_SEAT_ROW),
                        rs.getStr(FIELD_SEAT_COL),
                        rs.getStr(FIELD_START_STATION),
                        rs.getDate(FIELD_START_TIME),
                        rs.getStr(FIELD_END_STATION),
                        rs.getDate(FIELD_END_TIME),
                        rs.getStr(FIELD_SEAT_TYPE),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
                pageRs.add(ticket);
            }
            pageRs.setPage(dbRs.getPage()+1);
            pageRs.setTotalPage(dbRs.getTotalPage());
            pageRs.setPageSize(dbRs.getPageSize());
            pageRs.setTotal(dbRs.getTotal());
            return pageRs;
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    private long insert(Ticket ticket,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(ticket, true, isSelective);
            Entity insertEntity = Entity.create(TABLE);
            if (Objects.nonNull(insertValueMap)){
                insertValueMap.forEach((key,value) -> {
                    if (PRIMARY_KEY.equals(key)){
                        return;
                    }
                    insertEntity.set(key,value);
                });
            }
            insertEntity.set(PRIMARY_KEY, SnowflakeUtil.getSnowflakeId());
            return Db.use().insert(
                    insertEntity
            );
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0L;
        }
    }
}
