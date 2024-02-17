package com.jiawa.train.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiawa.train.business.entity.Station;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface StationMapper extends BaseMapper<Station> {


}
