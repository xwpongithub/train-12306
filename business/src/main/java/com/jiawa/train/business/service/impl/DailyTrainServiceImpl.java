package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiawa.train.business.entity.*;
import com.jiawa.train.business.enums.SeatTypeEnum;
import com.jiawa.train.business.enums.TrainTypeEnum;
import com.jiawa.train.business.mapper.*;
import com.jiawa.train.business.req.DailyTrainQueryReq;
import com.jiawa.train.business.req.DailyTrainSaveReq;
import com.jiawa.train.business.resp.DailyTrainQueryResp;
import com.jiawa.train.business.service.*;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DailyTrainServiceImpl implements IDailyTrainService {

    private final DailyTrainMapper dailyTrainMapper;

    @Override
    public void save(DailyTrainSaveReq req) {
        var now = DateUtil.date();
        var dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);
        if (Objects.isNull(dailyTrain.getId())) {
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateById(dailyTrain);
        }
    }

    @Override
    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req) {
        var p = new Page<DailyTrain>(req.getPage(),req.getSize());
        var q = new LambdaQueryWrapper<DailyTrain>();
        q
                .select(
                        DailyTrain::getId,
                        DailyTrain::getCode,
                        DailyTrain::getType,
                        DailyTrain::getStart,
                        DailyTrain::getStartPinyin,
                        DailyTrain::getStartTime,
                        DailyTrain::getEndVal,
                        DailyTrain::getEndPinyin,
                        DailyTrain::getEndTime,
                        DailyTrain::getCreateTime,
                        DailyTrain::getUpdateTime
                )
                .eq(Objects.nonNull(req.getDate()),DailyTrain::getDate,req.getDate())
                .eq(Objects.nonNull(req.getCode()),DailyTrain::getCode,req.getCode())
                .orderByDesc(DailyTrain::getDate)
                .orderByAsc(DailyTrain::getCode);

        var dbPage = dailyTrainMapper.selectPage(p,q);
        var resp = new PageResp<DailyTrainQueryResp>();
        var list = BeanUtil.copyToList(dbPage.getRecords() , DailyTrainQueryResp.class);
        resp.setTotal((int)dbPage.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainMapper.deleteById(id);
    }

    private final ITrainService trainService;
    private final ITrainStationService trainStationService;
    private final ITrainCarriageService trainCarriageService;
    private final ITrainSeatService trainSeatService;
    private final IDailyTrainSeatService dailyTrainSeatService;

    private final DailyTrainStationMapper dailyTrainStationMapper;
    private final DailyTrainCarriageMapper dailyTrainCarriageMapper;
    private final DailyTrainSeatMapper dailyTrainSeatMapper;
    private final DailyTrainTicketMapper dailyTrainTicketMapper;
    /**
     * 生成每日车次数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void genDaily(Date date) {
        var trainList = trainService.selectAll();
        if (CollUtil.isEmpty(trainList)) {
            LogUtil.info("没有车次基础数据，任务结束");
            return;
        }
        for (Train train : trainList) {
            genDailyTrainData(date, train);
        }
    }

    private void genDailyTrainData(Date date, Train train) {
        var delQ = new LambdaQueryWrapper<DailyTrain>();
        delQ
//                    .eq(DailyTrainEntity::getDate, date)
                .eq(DailyTrain::getCode, train.getCode());
        dailyTrainMapper.delete(delQ);

        var now = DateUtil.date();
        var newDailyTrain = BeanUtil.copyProperties(train,DailyTrain.class);
        newDailyTrain.setId(SnowflakeUtil.getSnowflakeId());
        newDailyTrain.setCreateTime(now);
        newDailyTrain.setUpdateTime(now);
        newDailyTrain.setDate(date);
        dailyTrainMapper.insert(newDailyTrain);

        // 生成每日车站数据
        genDailyTrainStationData(date,train.getCode());
        // 生成每日车厢数据
        genDailyTrainCarriageData(date,train.getCode());
        // 生成每日座位数据
        genDailyTrainSeatData(date,train.getCode());
        // 生成每日车次对应的余票信息
        genDailyTrainTicketData(date,train.getCode(),train.getType());
    }

    // 根据车次查询经过的所有车站
    private void genDailyTrainStationData(Date date,String trainCode) {
        var delStationQ = Wrappers.<DailyTrainStation>lambdaQuery();
        delStationQ
                    .eq(DailyTrainStation::getDate,date)
                .eq(DailyTrainStation::getTrainCode,trainCode);
        dailyTrainStationMapper.delete(delStationQ);

        var trainStationList = trainStationService.selectByTrainCode(trainCode);

        if (CollUtil.isEmpty(trainStationList)){
            LogUtil.info("车次{}没有车站基础数据，生成该车次的车站信息结束",trainCode);
            return;
        }

        for (TrainStation trainStation : trainStationList) {
            var now = DateUtil.date();
            var newDailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);
            newDailyTrainStation.setId(SnowflakeUtil.getSnowflakeId());
            newDailyTrainStation.setCreateTime(now);
            newDailyTrainStation.setUpdateTime(now);
            newDailyTrainStation.setDate(date);
            dailyTrainStationMapper.insert(newDailyTrainStation);
        }
    }

    private void genDailyTrainCarriageData(Date date,String trainCode) {
        var delCarriageQ = Wrappers.<DailyTrainCarriage>lambdaQuery();
        delCarriageQ
                    .eq(DailyTrainCarriage::getDate,date)
                .eq(DailyTrainCarriage::getTrainCode,trainCode);
        dailyTrainCarriageMapper.delete(delCarriageQ);

        var trainCarriageList = trainCarriageService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(trainCarriageList)){
            LogUtil.info("车次{}没有车厢基础数据，生成该车次的车厢信息结束",trainCode);
            return;
        }

        for (TrainCarriage trainCarriage : trainCarriageList) {
            var now = DateUtil.date();
            var newDailyTrainCarriage = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriage.class);
            newDailyTrainCarriage .setId(SnowflakeUtil.getSnowflakeId());
            newDailyTrainCarriage .setCreateTime(now);
            newDailyTrainCarriage .setUpdateTime(now);
            newDailyTrainCarriage .setDate(date);
            dailyTrainCarriageMapper.insert(newDailyTrainCarriage);
        }
    }


    private void genDailyTrainSeatData(Date date,String trainCode) {
        var delSeatQ = Wrappers.<DailyTrainSeat>lambdaQuery();
        delSeatQ
                    .eq(DailyTrainSeat::getDate,date)
                .eq(DailyTrainSeat::getTrainCode,trainCode);
        dailyTrainSeatMapper.delete(delSeatQ);

        var stationList = trainStationService.selectByTrainCode(trainCode);
        String sell = StrUtil.fillBefore("", '0', stationList.size() - 1);

        var trainSeatList = trainSeatService.selectByTrainCode(trainCode);

        if (CollUtil.isEmpty(trainSeatList)){
            LogUtil.info("车次{}没有座位基础数据，生成该车次的座位信息结束",trainCode);
            return;
        }

        for (TrainSeat trainSeat : trainSeatList) {
            var now = DateUtil.date();
            var newDailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
            newDailyTrainSeat.setId(SnowflakeUtil.getSnowflakeId());
            newDailyTrainSeat.setCreateTime(now);
            newDailyTrainSeat.setUpdateTime(now);
            newDailyTrainSeat.setDate(date);
            newDailyTrainSeat.setSell(sell);
            dailyTrainSeatMapper.insert(newDailyTrainSeat);
        }
    }

    private void genDailyTrainTicketData(Date date,String trainCode,String type) {
        // 删除某日某车次的余票信息
        var delTicketQ = Wrappers.<DailyTrainTicket>lambdaQuery();
        delTicketQ
                    .eq(DailyTrainTicket::getDate,date)
                .eq(DailyTrainTicket::getTrainCode,trainCode);
        dailyTrainTicketMapper.delete(delTicketQ);

        // 查询车次途经的所有车站信息

        var trainStationList = trainStationService.selectByTrainCode(trainCode);

        if (CollUtil.isEmpty(trainStationList)){
            LogUtil.info("车次{}没有车站基础数据，生成该车次的余票信息结束",trainCode);
            return;
        }

        var now = DateUtil.date();

        // 查询各种座位的余票数量
        var ydzCount = dailyTrainSeatService.countSeat(date,trainCode, SeatTypeEnum.YDZ.getCode());
        var edzCount = dailyTrainSeatService.countSeat(date,trainCode, SeatTypeEnum.EDZ.getCode());
        var rwCount = dailyTrainSeatService.countSeat(date,trainCode, SeatTypeEnum.RW.getCode());
        var ywCount = dailyTrainSeatService.countSeat(date,trainCode, SeatTypeEnum.YW.getCode());

        // 计算各种座位的票价 票价=里程之和*座位单价*车次类型系数
        // 阶梯加个是比较常见的涉及，比如：
        // 0~100公里:0.4元/公里
        // 100~200公里:0.3元/公里
        // 例： 做了一趟车,开了150公里，则票价=100*0.4+(150-100)*0.3

        // 计算票价系数
        var trainTypePriceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode,type);


        for (int i = 0,l = trainStationList.size(); i < l; i++) {
            // 得到出发站
            var startStation = trainStationList.get(i);

            var sumKM = BigDecimal.ZERO;
            for (int j = i+1; j < l; j++) {
                var endStation = trainStationList.get(j);

                sumKM = sumKM.add(endStation.getKm());

                var dailyTicket = new DailyTrainTicket();
                dailyTicket.setId(SnowflakeUtil.getSnowflakeId());
                dailyTicket.setCreateTime(now);
                dailyTicket.setUpdateTime(now);
                dailyTicket.setDate(date);
                dailyTicket.setTrainCode(trainCode);
                // Start 1.初始化余票信息
                dailyTicket.setStart(startStation.getName());
                dailyTicket.setStartPinyin(startStation.getNamePinyin());
                dailyTicket.setStartTime(startStation.getOutTime());
                dailyTicket.setStartIndex(startStation.getIndex());
                dailyTicket.setEndVal(endStation.getName());
                dailyTicket.setEndTime(endStation.getInTime());
                dailyTicket.setEndPinyin(endStation.getNamePinyin());
                dailyTicket.setEndIndex(endStation.getIndex());
                // End 1.初始化余票信息
                // Start 2.初始化各种座位的余票信息
                // 小数位四舍五入
                var ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(trainTypePriceRate).setScale(2, RoundingMode.HALF_UP);
                var edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(trainTypePriceRate).setScale(2, RoundingMode.HALF_UP);
                var rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(trainTypePriceRate).setScale(2, RoundingMode.HALF_UP);
                var ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(trainTypePriceRate).setScale(2, RoundingMode.HALF_UP);

                dailyTicket.setYdz(ydzCount);
                dailyTicket.setYdzPrice(ydzPrice);
                dailyTicket.setEdz(edzCount);
                dailyTicket.setEdzPrice(edzPrice);
                dailyTicket.setRw(rwCount);
                dailyTicket.setRwPrice(rwPrice);
                dailyTicket.setYw(ywCount);
                dailyTicket.setYwPrice(ywPrice);
                // End 2.初始化各种座位的余票信息
                dailyTrainTicketMapper.insert(dailyTicket);
            }
        }
    }

}
