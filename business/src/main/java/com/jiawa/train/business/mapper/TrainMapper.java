package com.jiawa.train.business.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.handler.RsHandler;
import com.jiawa.train.business.entity.Train;
import com.jiawa.train.common.toolkits.LogUtil;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class TrainMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "train";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_START= "start";
    private static final String FIELD_START_PINYIN= "start_pinyin";
    private static final String FIELD_START_TIME= "start_time";
    private static final String FIELD_END_VAL= "end_val";
    private static final String FIELD_END_PINYIN= "end_pinyin";
    private static final String FIELD_END_TIME= "end_time";
    private static final String FIELD_CREATE_TIME= "create_time";

    private static final String FIELD_UPDATE_TIME= "update_time";
    private static String getSelectSql() {
        return ArrayUtil.join(FIELDS,",");
    }

    private static final String[] FIELDS = {PRIMARY_KEY,
            FIELD_CODE,
            FIELD_TYPE,
            FIELD_START,
            FIELD_START_PINYIN,
            FIELD_START_TIME,
            FIELD_END_VAL,
            FIELD_END_PINYIN,
            FIELD_END_TIME,
            FIELD_CREATE_TIME,FIELD_UPDATE_TIME};

    public int deleteByPrimaryKey(Long id) {
        try {
            return Db.use().del(Entity.create(TABLE).set(PRIMARY_KEY, id));
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0;
        }
    }

    public long insert(Train train) {
        return insert(train, false);
    }

    public long insertSelective(Train train) {
        return insert(train,true);
    }

    private long insert(Train train,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(train, true, isSelective);
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

    public int updateSelective(Train train, Map<String,Object> conditions) {
        return update(train, conditions, true);
    }

    public int update(Train train,Map<String,Object> conditions) {
        return update(train, conditions, false);
    }

    private int update(Train train,Map<String,Object> conditions, boolean isSelective) {
        try {
            var updateValueMap = BeanUtil.beanToMap(train, true, isSelective);
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

    public Train selectByPrimaryKey(long id) {
        Entity example = Entity.create(TABLE).set(PRIMARY_KEY, id);
        try {
            return getTrain(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public Train selectOne(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return getTrain(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public List<Train> selectByExample(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return Db.use().find(example, Train.class);
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

    public PageResult<Train> page(int page, int size) {
        try{
            var dbRs = Db.use().page("select "+getSelectSql()+" from "+TABLE+" order by code asc", new Page(page-1, size));
            var pageRs = new PageResult<Train>();
            for (var rs : dbRs) {
                Train train = new Train(
                        rs.getLong(PRIMARY_KEY),
                        rs.getStr(FIELD_CODE),
                        rs.getStr(FIELD_TYPE),
                        rs.getStr(FIELD_START),
                        rs.getStr(FIELD_START_PINYIN),
                        rs.getDate(FIELD_START_TIME),
                        rs.getStr(FIELD_END_VAL),
                        rs.getStr(FIELD_END_PINYIN),
                        rs.getDate(FIELD_END_TIME),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
                pageRs.add(train);
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

    private Train getTrain(Entity example) throws SQLException {
        return Db.use().find(example, getRsHandler(),FIELDS);
    }

    private RsHandler<Train> getRsHandler() {
        return rs -> {
            Train train = null;
            if (rs.next()){
                train = new Train(
                        rs.getLong(PRIMARY_KEY),
                        rs.getString(FIELD_CODE),
                        rs.getString(FIELD_TYPE),
                        rs.getString(FIELD_START),
                        rs.getString(FIELD_START_PINYIN),
                        rs.getDate(FIELD_START_TIME),
                        rs.getString(FIELD_END_VAL),
                        rs.getString(FIELD_END_PINYIN),
                        rs.getDate(FIELD_END_TIME),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
            }
            return train;
        };
    }
}
