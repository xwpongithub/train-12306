package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
            stationMapper.insert(station);
        } else {
            station.setUpdateTime(now);
            stationMapper.updateById(station);
        }
    }

    @Override
    public void delete(Long id) {
       stationMapper.deleteById(id);
    }

    @Override
    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        var q = Wrappers.<Station>lambdaQuery();
        q.orderByDesc(Station::getId);
        var p = new Page<Station>(req.getPage(),req.getSize());
        var dbPage = stationMapper.selectPage(p,q);
        var resp = new PageResp<StationQueryResp>();
        var list = BeanUtil.copyToList(dbPage.getRecords(), StationQueryResp.class);
        resp.setTotal((int)dbPage.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public List<StationQueryResp> queryAll() {
        var q = Wrappers.<Station>lambdaQuery();
        q.orderByAsc(Station::getNamePy);
        var stationList = stationMapper.selectList(q);
        return BeanUtil.copyToList(stationList, StationQueryResp.class);
    }

    private Station selectByUnique(String name) {
        var q = Wrappers.<Station>lambdaQuery();
        q.eq(Station::getName,name);
        return stationMapper.selectOne(q);
    }

}
