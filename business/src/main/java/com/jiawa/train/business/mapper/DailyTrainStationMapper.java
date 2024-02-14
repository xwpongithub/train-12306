package com.jiawa.train.business.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.handler.RsHandler;
import com.jiawa.train.business.entity.DailyTrainStation;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.*;

@Repository
public class DailyTrainStationMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "daily_train_station";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_TRAIN_CODE= "train_code";
    private static final String FIELD_INDEX= "index";
    private static final String FIELD_NAME= "name";
    private static final String FIELD_NAME_PINYIN= "name_pinyin";
    private static final String FIELD_IN_TIME= "in_time";
    private static final String FIELD_OUT_TIME= "out_time";
    private static final String FIELD_STOP_TIME= "stop_time";
    private static final String FIELD_KM= "km";
    private static final String FIELD_CREATE_TIME = "create_time";
    private static final String FIELD_UPDATE_TIME= "update_time";
    private static String getSelectSql() {
        return ArrayUtil.join(FIELDS,",");
    }

    private static final String[] FIELDS = {
            PRIMARY_KEY,
            FIELD_DATE,
            FIELD_TRAIN_CODE,
            FIELD_INDEX,
            FIELD_NAME,
            FIELD_NAME_PINYIN,
            FIELD_IN_TIME,
            FIELD_OUT_TIME,
            FIELD_STOP_TIME,
            FIELD_KM,
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

    public long insert(DailyTrainStation dailyTrainStation) {
        return insert(dailyTrainStation, false);
    }

    public long insertSelective(DailyTrainStation dailyTrainStation) {
        return insert(dailyTrainStation,true);
    }

    private long insert(DailyTrainStation dailyTrainStation,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(dailyTrainStation, true, isSelective);
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

    public int updateSelective(DailyTrainStation dailyTrainStation, Map<String,Object> conditions) {
        return update(dailyTrainStation, conditions, true);
    }

    public int update(DailyTrainStation dailyTrainStation,Map<String,Object> conditions) {
        return update(dailyTrainStation, conditions, false);
    }

    private int update(DailyTrainStation dailyTrainStation,Map<String,Object> conditions, boolean isSelective) {
        try {
            var updateValueMap = BeanUtil.beanToMap(dailyTrainStation, true, isSelective);
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

    public DailyTrainStation selectByPrimaryKey(long id) {
        Entity example = Entity.create(TABLE).set(PRIMARY_KEY, id);
        try {
            return getDailyTrainStation(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public DailyTrainStation selectOne(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return getDailyTrainStation(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public List<DailyTrainStation> selectByExample(Map<String,Object> conditions) {
        try {
            List<Entity> entityList;
            var date = conditions.get("date");
            if (Objects.nonNull(date)) {
                entityList = Db.use().query("select " + getSelectSql() + " from " + TABLE + " where train_code = ? and date = ? order by index asc", conditions.get("train_code"),date);
            } else {
                entityList = Db.use().query("select " + getSelectSql() + " from " + TABLE + " where train_code = ? order by index asc", conditions.get("train_code"));
            }
            var list = new ArrayList<DailyTrainStation>();
            for (var rs : entityList) {
                DailyTrainStation dailyTrainStation = new DailyTrainStation (
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
                        rs.getStr(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_INDEX),
                        rs.getStr(FIELD_NAME),
                        rs.getStr(FIELD_NAME_PINYIN),
                        rs.getDate(FIELD_IN_TIME),
                        rs.getDate(FIELD_OUT_TIME),
                        rs.getDate(FIELD_STOP_TIME),
                        rs.getBigDecimal(FIELD_KM),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
                list.add(dailyTrainStation);
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

    public PageResult<DailyTrainStation> page(int page, int size, Date date,String code) {
        try{
            var sql = "select "+getSelectSql()+" from "+TABLE;
            var hasDate = false;
            var hasCode = false;
            if (Objects.nonNull(date) || StrUtil.isNotBlank(code)){
                sql+= " where ";
                if (Objects.nonNull(date)) {
                    hasDate = true;
                    sql+=" date = ? ";
                }
                if (StrUtil.isNotBlank(code)){
                    hasCode = true;
                    if (hasDate) {
                        sql+=" and ";
                    }
                    sql+= " train_code = ? ";
                }
            }
            sql+= " order by date desc,index asc";
            PageResult<Entity> dbRs;
            if (hasDate && hasCode) {
                dbRs = Db.use().page(sql, new Page(page-1, size),date,code);
            } else if (hasDate) {
                dbRs = Db.use().page(sql, new Page(page-1, size),date);
            } else if (hasCode) {
                dbRs = Db.use().page(sql, new Page(page-1, size),code);
            } else {
                dbRs = Db.use().page(sql, new Page(page-1, size));
            }
            var pageRs = new PageResult<DailyTrainStation>();
            for (var rs : dbRs) {
                DailyTrainStation trainStation = new DailyTrainStation (
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
                        rs.getStr(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_INDEX),
                        rs.getStr(FIELD_NAME),
                        rs.getStr(FIELD_NAME_PINYIN),
                        rs.getDate(FIELD_IN_TIME),
                        rs.getDate(FIELD_OUT_TIME),
                        rs.getDate(FIELD_STOP_TIME),
                        rs.getBigDecimal(FIELD_KM),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
                pageRs.add(trainStation);
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

    private DailyTrainStation getDailyTrainStation(Entity example) throws SQLException {
        return Db.use().find(example, getRsHandler(),FIELDS);
    }

    private RsHandler<DailyTrainStation> getRsHandler() {
        return rs -> {
            DailyTrainStation dailyTrainStation = null;
            if (rs.next()){
                dailyTrainStation = new DailyTrainStation(
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
                        rs.getString(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_INDEX),
                        rs.getString(FIELD_NAME),
                        rs.getString(FIELD_NAME_PINYIN),
                        rs.getDate(FIELD_IN_TIME),
                        rs.getDate(FIELD_OUT_TIME),
                        rs.getDate(FIELD_STOP_TIME),
                        rs.getBigDecimal(FIELD_KM),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
            }
            return dailyTrainStation;
        };
    }
}
