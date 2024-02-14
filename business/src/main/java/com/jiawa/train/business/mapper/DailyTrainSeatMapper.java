package com.jiawa.train.business.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.handler.RsHandler;
import com.jiawa.train.business.entity.DailyTrainSeat;
import com.jiawa.train.business.entity.TrainSeat;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class DailyTrainSeatMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "daily_train_seat";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_TRAIN_CODE = "train_code";
    private static final String FIELD_CARRIAGE_INDEX = "carriage_index";
    private static final String FIELD_ROW = "row";
    private static final String FIELD_COL = "col";
    private static final String FIELD_SEAT_TYPE = "seat_type";
    private static final String FIELD_CARRIAGE_SEAT_INDEX = "carriage_seat_index";
    private static final String FIELD_SELL = "sell";
    private static final String FIELD_CREATE_TIME = "create_time";
    private static final String FIELD_UPDATE_TIME= "update_time";
    private static String getSelectSql() {
        return ArrayUtil.join(FIELDS,",");
    }

    private static final String[] FIELDS = {
            PRIMARY_KEY,
            FIELD_DATE,
            FIELD_TRAIN_CODE,
            FIELD_CARRIAGE_INDEX,
            FIELD_ROW,
            FIELD_COL,
            FIELD_SEAT_TYPE,
            FIELD_CARRIAGE_SEAT_INDEX,
            FIELD_SELL,
            FIELD_CREATE_TIME,
            FIELD_UPDATE_TIME
    };

    public int deleteByPrimaryKey(Long id) {
        try {
            return Db.use().del(Entity.create(TABLE).set(PRIMARY_KEY, id));
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0;
        }
    }

    public long insert(DailyTrainSeat trainSeat) {
        return insert(trainSeat, false);
    }

    public long insertSelective(DailyTrainSeat trainSeat) {
        return insert(trainSeat,true);
    }

    private long insert(DailyTrainSeat trainSeat,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(trainSeat, true, isSelective);
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

    public int updateSelective(DailyTrainSeat trainSeat, Map<String,Object> conditions) {
        return update(trainSeat, conditions, true);
    }

    public int update(DailyTrainSeat trainSeat,Map<String,Object> conditions) {
        return update(trainSeat, conditions, false);
    }

    private int update(DailyTrainSeat trainSeat,Map<String,Object> conditions, boolean isSelective) {
        try {
            var updateValueMap = BeanUtil.beanToMap(trainSeat, true, isSelective);
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

    public DailyTrainSeat selectByPrimaryKey(long id) {
        Entity example = Entity.create(TABLE).set(PRIMARY_KEY, id);
        try {
            return getDailyTrainSeat(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public DailyTrainSeat selectOne(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return getDailyTrainSeat(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public List<DailyTrainSeat> selectByExample(Map<String,Object> conditions) {
        try {
            var sql = "select "+getSelectSql()+" from "+TABLE + " where train_code = ? and date = ? ";
            var carriageIndex = conditions.get("carriage_index");
            if (Objects.nonNull(carriageIndex)){
                sql += " and carriage_index = ? ";
            }
            sql+=" order by carriage_index asc,carriage_seat_index asc ";
            List<Entity> entityList;
            if (Objects.nonNull(carriageIndex)){
                entityList=Db.use().query(sql, conditions.get("train_code"),conditions.get("date"),conditions.get("carriage_index"));
            }else {
                entityList=Db.use().query(sql, conditions.get("train_code"),conditions.get("date"));
            }
            var list = new ArrayList<DailyTrainSeat>();
            for (var rs : entityList) {
                DailyTrainSeat trainSeat = new DailyTrainSeat(
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
                        rs.getStr(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_CARRIAGE_INDEX),
                        rs.getStr(FIELD_ROW),
                        rs.getStr(FIELD_COL),
                        rs.getStr(FIELD_SEAT_TYPE),
                        rs.getInt(FIELD_CARRIAGE_SEAT_INDEX),
                        rs.getStr(FIELD_SELL),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
                list.add(trainSeat);
            }
            return list;
        } catch (SQLException e) {
            LogUtil.error(e);
            return new ArrayList<>();
        }
    }

    public long countExample(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return Db.use().count(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0;
        }
    }

    public PageResult<DailyTrainSeat> page(int page, int size, String trainCode) {
        try{
            PageResult<Entity> dbRs;
            if (StrUtil.isNotBlank(trainCode)){
                dbRs = Db.use().page("select "+getSelectSql()+" from "+TABLE + " where train_code = ? order by train_code asc,carriage_index asc,carriage_seat_index asc", new Page(page-1, size),trainCode);
            } else {
                dbRs = Db.use().page("select "+getSelectSql()+" from "+TABLE+" order by train_code asc,carriage_index asc,carriage_seat_index asc", new Page(page-1, size));
            }
            var pageRs = new PageResult<DailyTrainSeat>();
            for (var rs : dbRs) {
                DailyTrainSeat dailyTrainSeat = new DailyTrainSeat(
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
                        rs.getStr(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_CARRIAGE_INDEX),
                        rs.getStr(FIELD_ROW),
                        rs.getStr(FIELD_COL),
                        rs.getStr(FIELD_SEAT_TYPE),
                        rs.getInt(FIELD_CARRIAGE_SEAT_INDEX),
                        rs.getStr(FIELD_SELL),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
                pageRs.add(dailyTrainSeat);
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

    private DailyTrainSeat getDailyTrainSeat(Entity example) throws SQLException {
        return Db.use().find(example, getRsHandler(),FIELDS);
    }

    private RsHandler<DailyTrainSeat> getRsHandler() {
        return rs -> {
            DailyTrainSeat dailyTrainSeat = null;
            if (rs.next()){
                dailyTrainSeat = new DailyTrainSeat(
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
                        rs.getString(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_CARRIAGE_INDEX),
                        rs.getString(FIELD_ROW),
                        rs.getString(FIELD_COL),
                        rs.getString(FIELD_SEAT_TYPE),
                        rs.getInt(FIELD_CARRIAGE_SEAT_INDEX),
                        rs.getString(FIELD_SELL),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
            }
            return dailyTrainSeat;
        };
    }
}
