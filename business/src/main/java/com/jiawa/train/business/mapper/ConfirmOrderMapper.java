package com.jiawa.train.business.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import com.jiawa.train.business.entity.ConfirmOrder;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

@Repository
public class ConfirmOrderMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "confirm_order";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_MEMBER_ID = "member_id";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_TRAIN_CODE= "train_code";
    private static final String FIELD_START= "start";
    private static final String FIELD_END_VAL= "end_val";
    private static final String FIELD_DAILY_TRAIN_TICKET_ID= "daily_train_ticket_id";
    private static final String FIELD_TICKETS = "tickets";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_CREATE_TIME = "create_time";
    private static final String FIELD_UPDATE_TIME = "update_time";

    private static String getSelectSql() {
        return ArrayUtil.join(FIELDS,",");
    }

    private static final String[] FIELDS = {
            PRIMARY_KEY,
            FIELD_MEMBER_ID,
            FIELD_DATE,
            FIELD_TRAIN_CODE,
            FIELD_START,
            FIELD_END_VAL,
            FIELD_DAILY_TRAIN_TICKET_ID,
            FIELD_TICKETS,
            FIELD_STATUS,
            FIELD_CREATE_TIME,
            FIELD_UPDATE_TIME
    };

    public long insert(ConfirmOrder confirmOrder) {
        return insert(confirmOrder, false);
    }

    public long insertSelective(ConfirmOrder confirmOrder) {
        return insert(confirmOrder,true);
    }

    private long insert(ConfirmOrder confirmOrder,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(confirmOrder, true, isSelective);
            Entity insertEntity = Entity.create(TABLE);
            if (Objects.nonNull(insertValueMap)){
                insertValueMap.forEach((key,value) -> {
                    if (PRIMARY_KEY.equals(key)){
                        return;
                    }
                    insertEntity.set(key,value);
                });
            }
            insertEntity.set("id", SnowflakeUtil.getSnowflakeId());
            return Db.use().insert(
                    insertEntity
            );
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0L;
        }
    }

    public PageResult<ConfirmOrder> page(int page, int size) {
        try{
            PageResult<Entity> dbRs = Db.use().page("select "+getSelectSql()+" from "+TABLE+" order by id desc", new Page(page-1, size));
            var pageRs = new PageResult<ConfirmOrder>();
            for (var rs : dbRs) {
                ConfirmOrder confirmOrder = new ConfirmOrder(
                        rs.getLong(PRIMARY_KEY),
                        rs.getLong(FIELD_MEMBER_ID),
                        rs.getDate(FIELD_DATE),
                        rs.getStr(FIELD_TRAIN_CODE),
                        rs.getStr(FIELD_START),
                        rs.getStr(FIELD_END_VAL),
                        rs.getLong(FIELD_DAILY_TRAIN_TICKET_ID),
                        rs.getStr(FIELD_STATUS),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME),
                        rs.getStr(FIELD_TICKETS)
                );
                pageRs.add(confirmOrder);
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

    public int updateSelective(ConfirmOrder confirmOrder, Map<String,Object> conditions) {
        return update(confirmOrder, conditions, true);
    }

    public int update(ConfirmOrder confirmOrder,Map<String,Object> conditions) {
        return update(confirmOrder, conditions, false);
    }

    private int update(ConfirmOrder confirmOrder,Map<String,Object> conditions, boolean isSelective) {
        try {
            var updateValueMap = BeanUtil.beanToMap(confirmOrder, true, isSelective);
            Entity updateEntity = Entity.create(TABLE);
            Entity updateConditions = Entity.create(TABLE);
            if(Objects.nonNull(conditions)){
                conditions.forEach(updateConditions::set);
            }
            if (Objects.nonNull(updateValueMap)){
                updateValueMap.forEach((key,value) -> {
                    if (PRIMARY_KEY.equals(key)){
                        return;
                    }
                    updateEntity.set(key,value);
                });
            }
            return Db.use().update(
                    updateEntity ,
                    updateConditions
            );
        } catch (Exception e) {
            LogUtil.error(e);
            return 0;
        }
    }

}
