package com.jiawa.train.member.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.RsHandler;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import com.jiawa.train.member.entity.Member;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class MemberMapper {

    private static final String SCHEMA = "public";
    private static final String TABLE_NAME = "member";
    private static final String TABLE = SCHEMA+"."+TABLE_NAME;
    private static final String PRIMARY_KEY = "id";
    private static final String FIELD_MOBILE = "mobile";

    private static final String[] FIELDS = {PRIMARY_KEY,FIELD_MOBILE};

    public int deleteByPrimaryKey(Long id) {
        try {
            return Db.use().del(Entity.create(TABLE).set(PRIMARY_KEY, id));
        } catch (SQLException e) {
            LogUtil.error(e);
            return 0;
        }
    }

    public long insert(Member member) {
        return insert(member, false);
    }

    public long insertSelective(Member member) {
        return insert(member,true);
    }

    private long insert(Member member,boolean isSelective) {
        try {
            var insertValueMap = BeanUtil.beanToMap(member, true, isSelective);
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

    public int updateSelective(Member member,Map<String,Object> conditions) {
        return update( member, conditions, true);
    }

    public int update(Member member,Map<String,Object> conditions) {
        return update( member, conditions, false);
    }

    private int update(Member member,Map<String,Object> conditions, boolean isSelective) {
        try {
            var updateValueMap = BeanUtil.beanToMap(member, true, isSelective);
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

    public Member selectByPrimaryKey(long id) {
        Entity example = Entity.create(TABLE).set(PRIMARY_KEY, id);
        try {
            return getMember(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public Member selectOne(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return getMember(example);
        } catch (SQLException e) {
            LogUtil.error(e);
            return null;
        }
    }

    public List<Member> selectByExample(Map<String,Object> conditions) {
        Entity example = Entity.create(TABLE);
        if(Objects.nonNull(conditions)){
            conditions.forEach(example::set);
        }
        try {
            return Db.use().find(example, Member.class);
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

    private Member getMember(Entity example) throws SQLException{
        return Db.use().find(example, (RsHandler<Member>) rs -> {
            Member member = null;
            if (rs.next()){
                member = new Member(
                        rs.getLong(PRIMARY_KEY),
                        rs.getString(FIELD_MOBILE)
                );
            }
            return member;
        },FIELDS);
    }

}
