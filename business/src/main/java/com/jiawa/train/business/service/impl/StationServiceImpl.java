package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.jiawa.train.business.entity.Station;
import com.jiawa.train.business.mapper.StationMapper;
import com.jiawa.train.business.req.StationQueryReq;
import com.jiawa.train.business.req.StationSaveReq;
import com.jiawa.train.business.resp.StationQueryResp;
import com.jiawa.train.business.service.IStationService;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements IStationService {

    private final StationMapper stationMapper;

    @Override
    public void save(StationSaveReq req) {
        var now = DateUtil.date();
        var station = BeanUtil.copyProperties(req, Station.class);
        if (Objects.isNull(station.getId())) {
            // 保存之前，先校验唯一键是否存在
            var stationDB = selectByUnique(req.getName());
            if (Objects.nonNull(stationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR);
            }
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insertSelective(station);
        } else {
            station.setUpdateTime(now);
            var queryMap = new HashMap<String,Object>();
            queryMap.put("id", station.getId());
            stationMapper.updateSelective(station,queryMap);
        }
    }

    @Override
    public void delete(Long id) {
       stationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        var stationList = stationMapper.page(req.getPage(), req.getSize());
        var resp = new PageResp<StationQueryResp>();
        var list = BeanUtil.copyToList(stationList, StationQueryResp.class);
        resp.setTotal(stationList.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public List<StationQueryResp> queryAll() {
        var stationList = stationMapper.selectByExample(null);
        return BeanUtil.copyToList(stationList, StationQueryResp.class);
    }

    private Station selectByUnique(String name) {
        var q = new HashMap<String,Object>();
        q.put("name",name);
        return stationMapper.selectOne(q);
    }

}
