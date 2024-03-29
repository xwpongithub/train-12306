package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.Session;
import com.jiawa.train.business.entity.*;
import com.jiawa.train.business.enums.SeatTypeEnum;
import com.jiawa.train.business.enums.TrainTypeEnum;
import com.jiawa.train.business.mapper.DailyTrainMapper;
import com.jiawa.train.business.req.DailyTrainQueryReq;
import com.jiawa.train.business.req.DailyTrainSaveReq;
import com.jiawa.train.business.resp.DailyTrainQueryResp;
import com.jiawa.train.business.service.*;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
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
            dailyTrainMapper.insertSelective(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            var q = new HashMap<String,Object>();
            q.put("id",req.getId());
            dailyTrainMapper.updateSelective(dailyTrain,q);
        }
    }

    @Override
    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req) {
        var trainList = dailyTrainMapper.page(req.getPage(), req.getSize(),req.getDate(),req.getCode());
        var resp = new PageResp<DailyTrainQueryResp>();
        var list = BeanUtil.copyToList(trainList , DailyTrainQueryResp.class);
        resp.setTotal(trainList.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainMapper.deleteByPrimaryKey(id);
    }

    private final ITrainService trainService;
    private final ITrainStationService trainStationService;
    private final ITrainCarriageService trainCarriageService;
    private final ITrainSeatService trainSeatService;

    private final IDailyTrainSeatService dailyTrainSeatService;
    /**
     * 生成每日车次数据
     */
    @Override
    public void genDaily(Date date) {
        var trainList = trainService.selectAll();
        if (CollUtil.isEmpty(trainList)) {
            LogUtil.info("没有车次基础数据，任务结束");
            return;
        }
        Session session = Session.create();
        try {
            session.beginTransaction();
            for (Train train : trainList) {
                genDailyTrainData(session,date, train);
            }
            session.commit();
        } catch (SQLException e) {
            session.quietRollback();
            LogUtil.error(e);
        }
    }

    private void genDailyTrainData(Session session, Date date, Train train) throws SQLException {
        Entity delExample = Entity.create("public.daily_train");
        delExample.set("date", date);
        delExample.set("code", train.getCode());
        session.del(delExample);

        var now = DateUtil.date();
        var newDailyTrain = BeanUtil.copyProperties(train,DailyTrain.class);
        newDailyTrain.setId(SnowflakeUtil.getSnowflakeId());
        newDailyTrain.setCreateTime(now);
        newDailyTrain.setUpdateTime(now);
        newDailyTrain.setDate(date);

        Entity insertExample = Entity.create("public.daily_train");
        var insertMaps = BeanUtil.beanToMap(newDailyTrain,true, true);
        insertMaps.forEach(insertExample::set);
        session.insert(insertExample);

        // 生成每日车站数据
        genDailyTrainStationData(session, date,train.getCode());
        // 生成每日车厢数据
        genDailyTrainCarriageData(session,date,train.getCode());
        // 生成每日座位数据
        genDailyTrainSeatData(session, date,train.getCode());
        // 生成每日车次对应的余票信息
        genDailyTrainTicketData(session,date,train.getCode(),train.getType());
    }

    // 根据车次查询经过的所有车站
    private void genDailyTrainStationData(Session session, Date date,String trainCode) throws SQLException {
        Entity delExample = Entity.create("public.daily_train_station");
        delExample.set("date", date);
        delExample.set("train_code", trainCode);
        session.del(delExample);

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

            Entity insertExample = Entity.create("public.daily_train_station");
            var insertMaps = BeanUtil.beanToMap(newDailyTrainStation,true, true);
            insertMaps.forEach(insertExample::set);
            session.insert(insertExample);
        }
    }

    private void genDailyTrainCarriageData(Session session, Date date,String trainCode) throws SQLException {
        Entity delExample = Entity.create("public.daily_train_carriage");
        delExample.set("date", date);
        delExample.set("train_code", trainCode);
        session.del(delExample);

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

            Entity insertExample = Entity.create("public.daily_train_carriage");
            var insertMaps = BeanUtil.beanToMap(newDailyTrainCarriage ,true, true);
            insertMaps.forEach(insertExample::set);
            session.insert(insertExample);
        }
    }


    private void genDailyTrainSeatData(Session session, Date date,String trainCode) throws SQLException {
        Entity delExample = Entity.create("public.daily_train_seat");
        delExample.set("date", date);
        delExample.set("train_code", trainCode);
        session.del(delExample);

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
            Entity insertExample = Entity.create("public.daily_train_seat");
            var insertMaps = BeanUtil.beanToMap(newDailyTrainSeat ,true, true);
            insertMaps.forEach(insertExample::set);
            session.insert(insertExample);
        }
    }

    private void genDailyTrainTicketData(Session session, Date date,String trainCode,String type) throws SQLException {
        Entity delExample = Entity.create("public.daily_train_ticket");
        delExample.set("date", date);
        delExample.set("train_code", trainCode);
        session.del(delExample);

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
                Entity insertExample = Entity.create("public.daily_train_ticket");
                var insertMaps = BeanUtil.beanToMap(dailyTicket ,true, true);
                insertMaps.forEach(insertExample::set);
                session.insert(insertExample);
            }
        }
    }

}
