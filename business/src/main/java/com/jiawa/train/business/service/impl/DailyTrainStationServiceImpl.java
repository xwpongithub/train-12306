package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.jiawa.train.business.entity.DailyTrainStation;
import com.jiawa.train.business.mapper.DailyTrainStationMapper;
import com.jiawa.train.business.req.DailyTrainStationQueryReq;
import com.jiawa.train.business.req.DailyTrainStationSaveReq;
import com.jiawa.train.business.resp.DailyTrainStationQueryResp;
import com.jiawa.train.business.service.IDailyTrainStationService;
import com.jiawa.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DailyTrainStationServiceImpl implements IDailyTrainStationService {

    private final DailyTrainStationMapper dailyTrainStationMapper;
//    private final TrainStationService trainStationService;

    @Override
    public void save(DailyTrainStationSaveReq req) {
        var now = DateUtil.date();
        var dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStation.class);
        if (Objects.isNull(dailyTrainStation.getId())) {
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.insertSelective(dailyTrainStation);
        } else {
            dailyTrainStation.setUpdateTime(now);
            var q = new HashMap<String,Object>();
            q.put("id",req.getId());
            dailyTrainStationMapper.updateSelective(dailyTrainStation,q);
        }
    }

    @Override
    public PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req) {
        var trainList = dailyTrainStationMapper.page(req.getPage(), req.getSize(),req.getDate(),req.getTrainCode());
        var resp = new PageResp<DailyTrainStationQueryResp>();
        var list = BeanUtil.copyToList(trainList , DailyTrainStationQueryResp.class);
        resp.setTotal(trainList.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainStationMapper.deleteByPrimaryKey(id);
    }

//    @Transactional(rollbackFor = Exception.class)
    @Override
    public void genDaily(Date date, String trainCode) {
//        log.info("生成日期【{}】车次【{}】的车站信息开始", DateUtil.formatDate(date), trainCode);
//
//        // 查出某车次的所有的车站信息
//        var stationList = trainStationService.selectByTrainCode(trainCode);
//        if (CollUtil.isEmpty(stationList)) {
//            log.info("该车次没有车站基础数据，生成该车次的车站信息结束");
//            return;
//        }
//
//        for (var trainStation : stationList) {
//            var now = DateUtil.date();
//            var dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStationEntity.class);
//            dailyTrainStation.setCreateTime(now);
//            dailyTrainStation.setUpdateTime(now);
//            dailyTrainStation.setDate(date);
//            dailyTrainStationMapper.insert(dailyTrainStation);
//        }
//        log.info("生成日期【{}】车次【{}】的车站信息结束", DateUtil.formatDate(date), trainCode);
    }

    /**
     * 按车次查询全部车站
     */
    @Override
    public long countByTrainCode(Date date, String trainCode) {
        var q = new HashMap<String,Object>();
        q.put("date",date);
        q.put("train_code",trainCode);
        return dailyTrainStationMapper.countExample(q);
    }

    /**
     * 按车次日期查询车站列表，用于界面显示一列车经过的车站
     */
    @Override
    public List<DailyTrainStationQueryResp> queryByTrain(Date date, String trainCode) {
        var q = new HashMap<String,Object>();
        q.put("date",date);
        q.put("train_code",trainCode);
        var list = dailyTrainStationMapper.selectByExample(q);
        return BeanUtil.copyToList(list, DailyTrainStationQueryResp.class);
    }

}
