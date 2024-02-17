package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiawa.train.business.entity.TrainStation;
import com.jiawa.train.business.mapper.TrainStationMapper;
import com.jiawa.train.business.req.TrainStationQueryReq;
import com.jiawa.train.business.req.TrainStationSaveReq;
import com.jiawa.train.business.resp.TrainStationQueryResp;
import com.jiawa.train.business.service.ITrainStationService;
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
public class TrainStationServiceImpl implements ITrainStationService {

    private final TrainStationMapper trainStationMapper;

    @Override
    public void save(TrainStationSaveReq req) {
        var now = DateUtil.date();
        var trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        if (Objects.isNull(trainStation.getId())) {
            // 保存之前，先校验唯一键是否存在
            var trainStationDB = selectByUnique(req.getTrainCode(), req.getIndex());
            if (Objects.nonNull(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR);
            }
            // 保存之前，先校验唯一键是否存在
            trainStationDB = selectByUnique(req.getTrainCode(), req.getName());
            if (Objects.nonNull(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR);
            }

            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        } else {
            trainStation.setUpdateTime(now);
            var q = new HashMap<String,Object>();
            q.put("id", req.getId());
            trainStationMapper.updateById(trainStation);
        }
    }


    @Override
    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req) {

        var q = Wrappers.<TrainStation>lambdaQuery();
        q.orderByAsc(TrainStation::getTrainCode)
                .eq(StrUtil.isNotBlank(req.getTrainCode()),TrainStation::getTrainCode,req.getTrainCode());
        var p = new Page<TrainStation>(req.getPage(),req.getSize());
        var dbPage = trainStationMapper.selectPage(p,q);
        var resp = new PageResp<TrainStationQueryResp>();
        var list = BeanUtil.copyToList(dbPage.getRecords() , TrainStationQueryResp.class);
        resp.setTotal((int)dbPage.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        trainStationMapper.deleteById(id);
    }

    @Override
    public List<TrainStation> selectByTrainCode(String trainCode) {
        var q = Wrappers.<TrainStation>lambdaQuery();
        q
                .eq(TrainStation::getTrainCode,trainCode)
                .orderByAsc(TrainStation::getIndex);
        return trainStationMapper.selectList(q);
    }

    private TrainStation selectByUnique(String trainCode, Integer index) {
        var q = Wrappers.<TrainStation>lambdaQuery();
        q.eq(TrainStation::getTrainCode,trainCode)
                .eq(TrainStation::getIndex,index);
        return trainStationMapper.selectOne(q);
    }


    private TrainStation selectByUnique(String trainCode, String name) {
        var q = Wrappers.<TrainStation>lambdaQuery();
        q.eq(TrainStation::getTrainCode,trainCode)
                .eq(TrainStation::getName,name);
        return trainStationMapper.selectOne(q);
    }

}
