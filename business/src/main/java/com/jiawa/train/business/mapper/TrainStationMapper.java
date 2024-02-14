package com.jiawa.train.business.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.handler.RsHandler;
import com.jiawa.train.business.entity.TrainStation;
import com.jiawa.train.common.toolkits.LogUtil;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class TrainStationMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "train_station";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
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

    public long insert(TrainStation trainStation) {
        return insert(trainStation, false);
    }

    public long insertSelective(TrainStation trainStation) {
        return insert(trainStation,true);
    }

    private long insert(TrainStation trainStation,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(trainStation, true, isSelective);
            Entity insertEntity = Entity.create(TABLE);
            if (Objects.nonNull(insertValueMap)){
                insertValueMap.forEach((key,value) -> {
                    if (PRIMARY_KEY.equals(key)){
                        return;
                    }
                    insertEntity.set(key,value);
                });
            }
            return Db.use().insertForGeneratedKey(
                    insertEntity
            );
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0L;
        }
    }

    public int updateSelective(TrainStation trainStation, Map<String,Object> conditions) {
        return update(trainStation, conditions, true);
    }

    public int update(TrainStation trainStation,Map<String,Object> conditions) {
        return update(trainStation, conditions, false);
    }

    private int update(TrainStation trainStation,Map<String,Object> conditions, boolean isSelective) {
        try {
            var updateValueMap = BeanUtil.beanToMap(trainStation, true, isSelective);
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

    public TrainStation selectByPrimaryKey(long id) {
        Entity example = Entity.create(TABLE).set(PRIMARY_KEY, id);
        try {
            return getTrainStation(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public TrainStation selectOne(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return getTrainStation(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public List<TrainStation> selectByExample(Map<String,Object> conditions) {
        try {
            var entityList = Db.use().query("select "+getSelectSql()+" from "+TABLE + " where train_code = ? order by index asc", conditions.get("train_code"));
            var list = new ArrayList<TrainStation>();
            for (var rs : entityList) {
                TrainStation trainStation = new TrainStation (
                        rs.getLong(PRIMARY_KEY),
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
                list.add(trainStation);
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

    public PageResult<TrainStation> page(int page, int size,String trainCode) {
        try{
            PageResult<Entity> dbRs;
            if (StrUtil.isNotBlank(trainCode)){
                dbRs = Db.use().page("select "+getSelectSql()+" from "+TABLE + " where train_code = ? order by train_code asc", new Page(page-1, size),trainCode);
            } else {
                dbRs = Db.use().page("select "+getSelectSql()+" from "+TABLE+" order by train_code asc", new Page(page-1, size));
            }
            var pageRs = new PageResult<TrainStation>();
            for (var rs : dbRs) {
                TrainStation trainStation = new TrainStation (
                        rs.getLong(PRIMARY_KEY),
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

    private TrainStation getTrainStation(Entity example) throws SQLException {
        return Db.use().find(example, getRsHandler(),FIELDS);
    }

    private RsHandler<TrainStation> getRsHandler() {
        return rs -> {
            TrainStation trainStation = null;
            if (rs.next()){
                trainStation = new TrainStation(
                        rs.getLong(PRIMARY_KEY),
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
            return trainStation;
        };
    }
}
