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
import com.jiawa.train.business.entity.DailyTrainTicket;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

@Repository
public class DailyTrainTicketMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "daily_train_ticket";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_TRAIN_CODE = "train_code";
    private static final String FIELD_START= "start";
    private static final String FIELD_START_PINYIN = "start_pinyin";
    private static final String FIELD_START_TIME = "start_time";
    private static final String FIELD_START_INDEX ="start_index";
    private static final String FIELD_END_VAL = "end_val";
    private static final String FIELD_END_PINYIN = "end_pinyin";
    private static final String FIELD_END_TIME = "end_time";
    private static final String FIELD_END_INDEX = "end_index";
    private static final String FIELD_YDZ = "ydz";
    private static final String FIELD_YDZ_PRICE = "ydz_price";
    private static final String FIELD_EDZ = "edz";
    private static final String FIELD_EDZ_PRICE ="edz_price";

    private static final String FIELD_RW ="rw";

    private static final String FIELD_RW_PRICE="rw_price";

    private static final String FIELD_YW ="yw";

    private static final String FIELD_YW_PRICE= "yw_price";
    private static final String FIELD_CREATE_TIME = "create_time";
    private static final String FIELD_UPDATE_TIME= "update_time";
    private static String getSelectSql() {
        return ArrayUtil.join(FIELDS,",");
    }

    private static final String[] FIELDS = {
            PRIMARY_KEY,
            FIELD_DATE,
            FIELD_TRAIN_CODE,
            FIELD_START,
            FIELD_START_PINYIN,
            FIELD_START_TIME,
            FIELD_START_INDEX,
            FIELD_END_VAL,
            FIELD_END_PINYIN,
            FIELD_END_TIME,
            FIELD_END_INDEX,
            FIELD_YDZ,
            FIELD_YDZ_PRICE,
            FIELD_EDZ,
            FIELD_EDZ_PRICE,
            FIELD_RW,
            FIELD_RW_PRICE,
            FIELD_YW,
            FIELD_YW_PRICE,
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

    public long insert(DailyTrainTicket dailyTrainTicket) {
        return insert(dailyTrainTicket, false);
    }

    public long insertSelective(DailyTrainTicket dailyTrainTicket) {
        return insert(dailyTrainTicket,true);
    }

    private long insert(DailyTrainTicket dailyTrainTicket,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(dailyTrainTicket, true, isSelective);
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

    public int updateSelective(DailyTrainTicket dailyTrainTicket, Map<String,Object> conditions) {
        return update(dailyTrainTicket, conditions, true);
    }

    public int update(DailyTrainTicket dailyTrainTicket,Map<String,Object> conditions) {
        return update(dailyTrainTicket, conditions, false);
    }

    private int update(DailyTrainTicket dailyTrainTicket,Map<String,Object> conditions, boolean isSelective) {
        try {
            var updateValueMap = BeanUtil.beanToMap(dailyTrainTicket, true, isSelective);
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

    public DailyTrainTicket selectByPrimaryKey(long id) {
        Entity example = Entity.create(TABLE).set(PRIMARY_KEY, id);
        try {
            return getDailyTrainTicket(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public DailyTrainTicket selectOne(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return getDailyTrainTicket(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
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

    public PageResult<DailyTrainTicket> page(int page, int size, Date date,String trainCode,String start,String end) {
        try{
            var sql = "select "+getSelectSql()+" from "+TABLE;
            var hasDate = false;
            var hasCode = false;
            var hasStart= false;
            var hasEnd = false;
            if (Objects.nonNull(date) || StrUtil.isNotBlank(trainCode) || StrUtil.isNotBlank(start) || StrUtil.isNotBlank(end)){
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
                if (StrUtil.isNotBlank(start)){
                    hasStart = true;
                    if (hasDate || hasCode){
                      sql+=" and ";
                    }
                    sql+=" start like ?";
                }
                if (StrUtil.isNotBlank(end)){
                    hasEnd = true;
                    if (hasDate || hasCode || hasStart){
                        sql+=" and ";
                    }
                    sql+=" end_val like ?";
                }
            }
            sql+= " order by date desc,start_time asc,train_code asc,start_index asc,end_index asc";
            PageResult<Entity> dbRs;
            if (hasDate && hasCode && hasStart && hasEnd) {
                dbRs = Db.use().page(sql, new Page(page-1, size),date,trainCode,start+"%",end+"%");
            } else if (hasDate && hasCode && hasStart) {
                dbRs = Db.use().page(sql, new Page(page-1, size),date,trainCode,start+"%");
            }  else if (hasDate && hasStart && hasEnd) {
                dbRs = Db.use().page(sql, new Page(page-1, size),date,start+"%",end+"%");
            } else if (hasDate && hasCode && hasEnd) {
                dbRs = Db.use().page(sql, new Page(page-1, size),date,trainCode,end+"%");
            } else if (hasDate && hasCode) {
                dbRs = Db.use().page(sql, new Page(page-1, size),date,trainCode);
            }  else if (hasDate && hasStart) {
                dbRs = Db.use().page(sql, new Page(page-1, size),date,start+"%");
            } else if (hasDate && hasEnd) {
                dbRs = Db.use().page(sql, new Page(page-1, size),date,end+"%");
            } else if (hasCode && hasStart) {
                dbRs = Db.use().page(sql, new Page(page-1, size),trainCode,start+"%");
            } else if (hasCode && hasEnd) {
                dbRs = Db.use().page(sql, new Page(page-1, size),trainCode,end+"%");
            } else if (hasStart && hasEnd) {
                dbRs = Db.use().page(sql, new Page(page-1, size),start+"%",end+"%");
            } else if (hasStart) {
                dbRs = Db.use().page(sql, new Page(page-1, size),start+"%");
            } else if (hasEnd) {
                dbRs = Db.use().page(sql, new Page(page-1, size),end+"%");
            } else if (hasCode) {
                dbRs = Db.use().page(sql, new Page(page-1, size),trainCode);
            }else {
                dbRs = Db.use().page(sql, new Page(page-1, size));
            }
            var pageRs = new PageResult<DailyTrainTicket>();
            for (var rs : dbRs) {
                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket(
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
                        rs.getStr(FIELD_TRAIN_CODE),
                        rs.getStr(FIELD_START),
                        rs.getStr(FIELD_START_PINYIN),
                        rs.getDate(FIELD_START_TIME),
                        rs.getInt(FIELD_START_INDEX),
                        rs.getStr(FIELD_END_VAL),
                        rs.getStr(FIELD_END_PINYIN),
                        rs.getDate(FIELD_END_TIME),
                        rs.getInt(FIELD_END_INDEX),
                        rs.getInt(FIELD_YDZ),
                        rs.getBigDecimal(FIELD_YDZ_PRICE),
                        rs.getInt(FIELD_EDZ),
                        rs.getBigDecimal(FIELD_EDZ_PRICE),
                        rs.getInt(FIELD_RW),
                        rs.getBigDecimal(FIELD_RW_PRICE),
                        rs.getInt(FIELD_YW),
                        rs.getBigDecimal(FIELD_YW_PRICE),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
                pageRs.add(dailyTrainTicket);
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

    private DailyTrainTicket getDailyTrainTicket(Entity example) throws SQLException {
        return Db.use().find(example, getRsHandler(),FIELDS);
    }

    private RsHandler<DailyTrainTicket> getRsHandler() {
        return rs -> {
            DailyTrainTicket dailyTrainTicket = null;
            if (rs.next()){
                dailyTrainTicket = new DailyTrainTicket(
                        rs.getLong(PRIMARY_KEY),
                        rs.getDate(FIELD_DATE),
                        rs.getString(FIELD_TRAIN_CODE),
                        rs.getString(FIELD_START),
                        rs.getString(FIELD_START_PINYIN),
                        rs.getDate(FIELD_START_TIME),
                        rs.getInt(FIELD_START_INDEX),
                        rs.getString(FIELD_END_VAL),
                        rs.getString(FIELD_END_PINYIN),
                        rs.getDate(FIELD_END_TIME),
                        rs.getInt(FIELD_END_INDEX),
                        rs.getInt(FIELD_YDZ),
                        rs.getBigDecimal(FIELD_YDZ_PRICE),
                        rs.getInt(FIELD_EDZ),
                        rs.getBigDecimal(FIELD_EDZ_PRICE),
                        rs.getInt(FIELD_RW),
                        rs.getBigDecimal(FIELD_RW_PRICE),
                        rs.getInt(FIELD_YW),
                        rs.getBigDecimal(FIELD_YW_PRICE),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
            }
            return dailyTrainTicket;
        };
    }

}
