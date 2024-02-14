package com.jiawa.train.business.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.handler.RsHandler;
import com.jiawa.train.business.entity.DailyTrain;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.*;

@Repository
public class DailyTrainMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "daily_train";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_START = "start";
    private static final String FIELD_START_PINYIN = "start_pinyin";
    private static final String FIELD_START_TIME = "start_time";
    private static final String FIELD_END_VAL = "end_val";
    private static final String FIELD_END_PINYIN = "end_pinyin";
    private static final String FIELD_END_TIME = "end_time";
    private static final String FIELD_CREATE_TIME= "create_time";

    private static final String FIELD_UPDATE_TIME= "update_time";
    private static String getSelectSql() {
        return ArrayUtil.join(FIELDS,",");
    }

    private static final String[] FIELDS = {PRIMARY_KEY,
            FIELD_DATE,
            FIELD_CODE,
            FIELD_TYPE,
            FIELD_START,
            FIELD_START_PINYIN,
            FIELD_START_TIME,
            FIELD_END_VAL,
            FIELD_END_PINYIN,
            FIELD_END_TIME,
            FIELD_CREATE_TIME,
            FIELD_UPDATE_TIME};

    public int deleteByPrimaryKey(Long id) {
        try {
            return Db.use().del(Entity.create(TABLE).set(PRIMARY_KEY, id));
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0;
        }
    }

    public long insert(DailyTrain dailyTrain) {
        return insert(dailyTrain, false);
    }

    public long insertSelective(DailyTrain dailyTrain) {
        return insert(dailyTrain,true);
    }

    private long insert(DailyTrain dailyTrain,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(dailyTrain, true, isSelective);
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

    public int updateSelective(DailyTrain dailyTrain, Map<String,Object> conditions) {
        return update(dailyTrain, conditions, true);
    }

    public int update(DailyTrain dailyTrain,Map<String,Object> conditions) {
        return update(dailyTrain, conditions, false);
    }

    private int update(DailyTrain dailyTrain,Map<String,Object> conditions, boolean isSelective) {
        try {
            var updateValueMap = BeanUtil.beanToMap(dailyTrain, true, isSelective);
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

    public DailyTrain selectByPrimaryKey(long id) {
        Entity example = Entity.create(TABLE).set(PRIMARY_KEY, id);
        try {
            return getDailyTrain(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public DailyTrain selectOne(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return getDailyTrain(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public List<DailyTrain> selectByExample(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return Db.use().find(example, DailyTrain.class);
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

    public PageResult<DailyTrain> page(int page, int size, Date date,String code) {
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
                    sql+= " code = ? ";
                }
            }
                sql+= " order by date desc,code asc";
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
            var pageRs = new PageResult<DailyTrain>();
            for (var rs : dbRs) {
                DailyTrain train = new DailyTrain(
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
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

    private DailyTrain getDailyTrain(Entity example) throws SQLException {
        return Db.use().find(example, getRsHandler(),FIELDS);
    }

    private RsHandler<DailyTrain> getRsHandler() {
        return rs -> {
            DailyTrain dailyTrain = null;
            if (rs.next()){
                dailyTrain = new DailyTrain(
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
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
            return dailyTrain;
        };
    }

    public int delete(Map<String, Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return Db.use().del(
                    example
            );
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0;
        }
    }
}
