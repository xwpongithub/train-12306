package com.jiawa.train.business.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.handler.RsHandler;
import com.jiawa.train.business.entity.DailyTrainCarriage;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.*;

@Repository
public class DailyTrainCarriageMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "daily_train_carriage";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_TRAIN_CODE= "train_code";
    private static final String FIELD_INDEX= "index";
    private static final String FIELD_SEAT_TYPE= "seat_type";
    private static final String FIELD_SEAT_COUNT= "seat_count";
    private static final String FIELD_ROW_COUNT= "row_count";
    private static final String FIELD_COL_COUNT= "col_count";
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
            FIELD_SEAT_TYPE,
            FIELD_SEAT_COUNT,
            FIELD_ROW_COUNT,
            FIELD_COL_COUNT,
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

    public long insert(DailyTrainCarriage trainCarriage) {
        return insert(trainCarriage, false);
    }

    public long insertSelective(DailyTrainCarriage trainCarriage) {
        return insert(trainCarriage,true);
    }

    private long insert(DailyTrainCarriage trainCarriage,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(trainCarriage, true, isSelective);
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

    public int updateSelective(DailyTrainCarriage trainCarriage, Map<String,Object> conditions) {
        return update(trainCarriage, conditions, true);
    }

    public int update(DailyTrainCarriage trainCarriage,Map<String,Object> conditions) {
        return update(trainCarriage, conditions, false);
    }

    private int update(DailyTrainCarriage trainCarriage,Map<String,Object> conditions, boolean isSelective) {
        try {
            var updateValueMap = BeanUtil.beanToMap(trainCarriage, true, isSelective);
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

    public DailyTrainCarriage selectByPrimaryKey(long id) {
        Entity example = Entity.create(TABLE).set(PRIMARY_KEY, id);
        try {
            return getDailyTrainCarriage(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public DailyTrainCarriage selectOne(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return getDailyTrainCarriage(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public List<DailyTrainCarriage> selectByExample(Map<String,Object> conditions) {
        try {
            var entityList = Db.use().query("select "+getSelectSql()+" from "+TABLE + " where train_code = ? order by index asc", conditions.get("train_code"));
            var list = new ArrayList<DailyTrainCarriage>();
            for (var rs : entityList) {
                DailyTrainCarriage trainCarriage = new DailyTrainCarriage(
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
                        rs.getStr(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_INDEX),
                        rs.getStr(FIELD_SEAT_TYPE),
                        rs.getInt(FIELD_SEAT_COUNT),
                        rs.getInt(FIELD_ROW_COUNT),
                        rs.getInt(FIELD_COL_COUNT),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
                list.add(trainCarriage);
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

    public PageResult<DailyTrainCarriage> page(int page, int size, Date date, String trainCode) {
        try{
            var sql = "select "+getSelectSql()+" from "+TABLE;
            var hasDate = false;
            var hasCode = false;
            if (Objects.nonNull(date) || StrUtil.isNotBlank(trainCode)){
                sql+= " where ";
                if (Objects.nonNull(date)) {
                    hasDate = true;
                    sql+=" date = ? ";
                }
                if (StrUtil.isNotBlank(trainCode)){
                    hasCode = true;
                    if (hasDate) {
                        sql+=" and ";
                    }
                    sql+= " train_code = ? ";
                }
            }
            sql+= " order by train_code asc,index asc";
            PageResult<Entity> dbRs;
            if (hasDate && hasCode) {
                dbRs = Db.use().page(sql, new Page(page-1, size),date,trainCode);
            } else if (hasDate) {
                dbRs = Db.use().page(sql, new Page(page-1, size),date);
            } else if (hasCode) {
                dbRs = Db.use().page(sql, new Page(page-1, size),trainCode);
            } else {
                dbRs = Db.use().page(sql, new Page(page-1, size));
            }
            var pageRs = new PageResult<DailyTrainCarriage>();
            for (var rs : dbRs) {
                DailyTrainCarriage trainCarriage = new DailyTrainCarriage(
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
                        rs.getStr(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_INDEX),
                        rs.getStr(FIELD_SEAT_TYPE),
                        rs.getInt(FIELD_SEAT_COUNT),
                        rs.getInt(FIELD_ROW_COUNT),
                        rs.getInt(FIELD_COL_COUNT),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
                pageRs.add(trainCarriage);
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

    private DailyTrainCarriage getDailyTrainCarriage(Entity example) throws SQLException {
        return Db.use().find(example, getRsHandler(),FIELDS);
    }

    private RsHandler<DailyTrainCarriage> getRsHandler() {
        return rs -> {
            DailyTrainCarriage trainCarriage = null;
            if (rs.next()){
                trainCarriage = new DailyTrainCarriage(
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
                        rs.getString(FIELD_TRAIN_CODE),
                        rs.getInt(FIELD_INDEX),
                        rs.getString(FIELD_SEAT_TYPE),
                        rs.getInt(FIELD_SEAT_COUNT),
                        rs.getInt(FIELD_ROW_COUNT),
                        rs.getInt(FIELD_COL_COUNT),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
            }
            return trainCarriage;
        };
    }

}
