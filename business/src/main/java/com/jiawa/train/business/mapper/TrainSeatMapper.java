package com.jiawa.train.business.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.handler.RsHandler;
import com.jiawa.train.business.entity.TrainSeat;
import com.jiawa.train.common.toolkits.LogUtil;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class TrainSeatMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "train_seat";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_TRAIN_CODE = "train_code";
    private static final String FIELD_CARRIAGE_INDEX = "carriage_index";
    private static final String FIELD_ROW = "row";
    private static final String FIELD_COL = "col";
    private static final String FIELD_SEAT_TYPE = "seat_type";
    private static final String FIELD_CARRIAGE_SEAT_INDEX = "carriage_seat_index";
    private static final String FIELD_CREATE_TIME = "create_time";
    private static final String FIELD_UPDATE_TIME= "update_time";
    private static String getSelectSql() {
        return ArrayUtil.join(FIELDS,",");
    }

    private static final String[] FIELDS = {
            PRIMARY_KEY,
            FIELD_TRAIN_CODE,
            FIELD_CARRIAGE_INDEX,
            FIELD_ROW,
            FIELD_COL,
            FIELD_SEAT_TYPE,
            FIELD_CARRIAGE_SEAT_INDEX,
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

    public long insert(TrainSeat trainSeat) {
        return insert(trainSeat, false);
    }

    public long insertSelective(TrainSeat trainSeat) {
        return insert(trainSeat,true);
    }

    private long insert(TrainSeat trainSeat,boolean isSelective) {
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
            return Db.use().insertForGeneratedKey(
                    insertEntity
            );
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0L;
        }
    }

    public int updateSelective(TrainSeat trainSeat, Map<String,Object> conditions) {
        return update(trainSeat, conditions, true);
    }

    public int update(TrainSeat trainSeat,Map<String,Object> conditions) {
        return update(trainSeat, conditions, false);
    }

    private int update(TrainSeat trainSeat,Map<String,Object> conditions, boolean isSelective) {
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

    public TrainSeat selectByPrimaryKey(long id) {
        Entity example = Entity.create(TABLE).set(PRIMARY_KEY, id);
        try {
            return getTrainSeat(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public TrainSeat selectOne(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return getTrainSeat(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public List<TrainSeat> selectByExample(Map<String,Object> conditions) {
        try {
            var entityList = Db.use().query("select "+getSelectSql()+" from "+TABLE + " where train_code = ? order by id asc", conditions.get("train_code"));
            var list = new ArrayList<TrainSeat>();
            for (var rs : entityList) {
                TrainSeat trainSeat = new TrainSeat(
                        rs.getLong(PRIMARY_KEY),
                        rs.getStr(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_CARRIAGE_INDEX),
                        rs.getStr(FIELD_ROW),
                        rs.getStr(FIELD_COL),
                        rs.getStr(FIELD_SEAT_TYPE),
                        rs.getInt(FIELD_CARRIAGE_SEAT_INDEX),
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

    public PageResult<TrainSeat> page(int page, int size,String trainCode) {
        try{
            PageResult<Entity> dbRs;
            if (StrUtil.isNotBlank(trainCode)){
                dbRs = Db.use().page("select "+getSelectSql()+" from "+TABLE + " where train_code = ? order by train_code asc,carriage_index asc,carriage_seat_index asc", new Page(page-1, size),trainCode);
            } else {
                dbRs = Db.use().page("select "+getSelectSql()+" from "+TABLE+" order by train_code asc,carriage_index asc,carriage_seat_index asc", new Page(page-1, size));
            }
            var pageRs = new PageResult<TrainSeat>();
            for (var rs : dbRs) {
                TrainSeat trainStation = new TrainSeat(
                        rs.getLong(PRIMARY_KEY),
                        rs.getStr(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_CARRIAGE_INDEX),
                        rs.getStr(FIELD_ROW),
                        rs.getStr(FIELD_COL),
                        rs.getStr(FIELD_SEAT_TYPE),
                        rs.getInt(FIELD_CARRIAGE_SEAT_INDEX),
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

    private TrainSeat getTrainSeat(Entity example) throws SQLException {
        return Db.use().find(example, getRsHandler(),FIELDS);
    }

    private RsHandler<TrainSeat> getRsHandler() {
        return rs -> {
            TrainSeat trainSeat = null;
            if (rs.next()){
                trainSeat = new TrainSeat(
                        rs.getLong(PRIMARY_KEY),
                        rs.getString(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_CARRIAGE_INDEX),
                        rs.getString(FIELD_ROW),
                        rs.getString(FIELD_COL),
                        rs.getString(FIELD_SEAT_TYPE),
                        rs.getInt(FIELD_CARRIAGE_SEAT_INDEX),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
            }
            return trainSeat;
        };
    }
}
