package com.jiawa.train.business.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.handler.RsHandler;
import com.jiawa.train.business.entity.Station;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class StationMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "station";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_PINYIN = "name_pinyin";

    private static final String FIELD_PY= "name_py";

    private static final String FIELD_CREATE_TIME= "create_time";

    private static final String FIELD_UPDATE_TIME= "update_time";
    private static String getSelectSql() {
        return ArrayUtil.join(FIELDS,",");
    }

    private static final String[] FIELDS = {PRIMARY_KEY,FIELD_NAME,FIELD_PINYIN,FIELD_PY, FIELD_CREATE_TIME,FIELD_UPDATE_TIME};

    public int deleteByPrimaryKey(Long id) {
        try {
            return Db.use().del(Entity.create(TABLE).set(PRIMARY_KEY, id));
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0;
        }
    }

    public long insert(Station station) {
        return insert(station, false);
    }

    public long insertSelective(Station station) {
        return insert(station,true);
    }

    private long insert(Station station,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(station, true, isSelective);
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

    public int updateSelective(Station station, Map<String,Object> conditions) {
        return update(station, conditions, true);
    }

    public int update(Station station,Map<String,Object> conditions) {
        return update(station, conditions, false);
    }

    private int update(Station station,Map<String,Object> conditions, boolean isSelective) {
        try {
            var updateValueMap = BeanUtil.beanToMap(station, true, isSelective);
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

    public Station selectByPrimaryKey(long id) {
        Entity example = Entity.create(TABLE).set(PRIMARY_KEY, id);
        try {
            return getStation(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public Station selectOne(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return getStation(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public List<Station> selectByExample(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return Db.use().find(example, Station.class);
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

    public PageResult<Station> page(int page, int size) {
        try{
            var dbRs = Db.use().page("select "+getSelectSql()+" from "+TABLE+" order by id asc", new Page(page-1, size));
            var pageRs = new PageResult<Station>();
            for (var rs : dbRs) {
                Station station = new Station(
                        rs.getLong(PRIMARY_KEY),
                        rs.getStr(FIELD_NAME),
                        rs.getStr(FIELD_PINYIN),
                        rs.getStr(FIELD_PY),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
                pageRs.add(station);
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

    private Station getStation(Entity example) throws SQLException {
        return Db.use().find(example, getRsHandler(),FIELDS);
    }

    private RsHandler<Station> getRsHandler() {
        return rs -> {
            Station station = null;
            if (rs.next()){
                station = new Station(
                        rs.getLong(PRIMARY_KEY),
                        rs.getString(FIELD_NAME),
                        rs.getString(FIELD_PINYIN),
                        rs.getString(FIELD_PY),
                        rs.getDate(FIELD_CREATE_TIME),
                        rs.getDate(FIELD_UPDATE_TIME)
                );
            }
            return station;
        };
    }

}
