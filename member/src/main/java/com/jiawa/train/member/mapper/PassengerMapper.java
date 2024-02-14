package com.jiawa.train.member.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.handler.RsHandler;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import com.jiawa.train.member.entity.Passenger;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.*;

@Repository
public class PassengerMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "passenger";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_MEMBER_ID = "member_id";

    private static final String FIELD_NAME = "name";

    private static final String FIELD_ID_CARD = "id_card";

    private static final String FIELD_TYPE = "type";

    private static final String FIELD_CREATE_TIME = "create_time";

    private static final String FIELD_UPDATE_TIME = "update_time";


    private static final String[] FIELDS = {PRIMARY_KEY,FIELD_MEMBER_ID,FIELD_NAME,FIELD_ID_CARD,FIELD_TYPE,FIELD_CREATE_TIME,FIELD_UPDATE_TIME };

    private static String getSelectSql() {
        return ArrayUtil.join(FIELDS,",");
    }

    public int deleteByPrimaryKey(Long id) {
        try {
            return Db.use().del(Entity.create(TABLE).set(PRIMARY_KEY, id));
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0;
        }
    }

    public long insert(Passenger passenger) {
        return insert(passenger, false);
    }

    public long insertSelective(Passenger passenger) {
        return insert(passenger,true);
    }

    private long insert(Passenger passenger,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(passenger, true, isSelective);
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

    public int updateSelective(Passenger passenger,Map<String,Object> conditions) {
        return update(passenger, conditions, true);
    }

    public int update(Passenger passenger,Map<String,Object> conditions) {
        return update(passenger, conditions, false);
    }

    public int updateByPrimaryKey(Passenger passenger) {
        var conditions = new HashMap<String,Object>();
        conditions.put(PRIMARY_KEY, passenger.getId());
        return updateSelective(passenger,conditions);
    }

    private int update(Passenger passenger,Map<String,Object> conditions, boolean isSelective) {
        try {
            var updateValueMap = BeanUtil.beanToMap(passenger, true, isSelective);
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

    public Passenger selectByPrimaryKey(long id) {
        Entity example = Entity.create(TABLE).set(PRIMARY_KEY, id);
        try {
            return getPassenger(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public Passenger selectOne(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return getPassenger(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public List<Passenger> selectByExample(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return Db.use().find(example, Passenger.class);
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

    public PageResult<Passenger> pageByMember(int page, int size, long memberId) {
        try{
            var dbRs = Db.use().page("select "+getSelectSql()+" from "+TABLE+" where member_id = ? order by id asc", new Page(page-1, size),memberId);
            var pageRs = new PageResult<Passenger>();
            for (var rs : dbRs) {
                Passenger passenger = new Passenger(
                        rs.getLong(PRIMARY_KEY),
                        rs.getLong(FIELD_MEMBER_ID),
                        rs.getStr(FIELD_NAME),
                        rs.getStr(FIELD_ID_CARD),
                        rs.getStr(FIELD_TYPE),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
                pageRs.add(passenger);
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

    private Passenger getPassenger(Entity example) throws SQLException {
        return Db.use().find(example, getRsHandler(),FIELDS);
    }

    private RsHandler<Passenger> getRsHandler() {
        return rs -> {
            Passenger passenger = null;
            if (rs.next()){
                passenger = new Passenger(
                        rs.getLong(PRIMARY_KEY),
                        rs.getLong(FIELD_MEMBER_ID),
                        rs.getString(FIELD_NAME),
                        rs.getString(FIELD_ID_CARD),
                        rs.getString(FIELD_TYPE),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
            }
            return passenger;
        };
    }

}
