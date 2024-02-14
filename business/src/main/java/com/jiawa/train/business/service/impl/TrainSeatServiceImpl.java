package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.jiawa.train.business.entity.TrainSeat;
import com.jiawa.train.business.mapper.TrainSeatMapper;
import com.jiawa.train.business.req.TrainSeatQueryReq;
import com.jiawa.train.business.req.TrainSeatSaveReq;
import com.jiawa.train.business.resp.TrainSeatQueryResp;
import com.jiawa.train.business.service.ITrainSeatService;
import com.jiawa.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TrainSeatServiceImpl implements ITrainSeatService {


    private final TrainSeatMapper trainSeatMapper;
//    private final TrainCarriageService trainCarriageService;

    @Override
    public void save(TrainSeatSaveReq req) {
        var now = DateUtil.date();
        var trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if (Objects.isNull(trainSeat.getId())) {
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insertSelective(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            var q = new HashMap<String,Object>();
            q.put("id",req.getId());
            trainSeatMapper.updateSelective(trainSeat,q);
        }
    }

    @Override
    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
        var trainCarriageList = trainSeatMapper.page(req.getPage(), req.getSize(),req.getTrainCode());
        var resp = new PageResp<TrainSeatQueryResp>();
        var list = BeanUtil.copyToList(trainCarriageList , TrainSeatQueryResp.class);
        resp.setTotal(trainCarriageList.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        trainSeatMapper.deleteByPrimaryKey(id);
    }

//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void genTrainSeat(String trainCode) {
//        var now = DateUtil.date();
//        // 清空当前车次下的所有的座位记录
//        var q = Wrappers.<TrainSeatEntity>lambdaQuery();
//        q.eq(TrainSeatEntity::getTrainCode,trainCode);
//        trainSeatMapper.delete(q);
//
//        // 查找当前车次下的所有的车厢
//        var carriageList = trainCarriageService.selectByTrainCode(trainCode);
//        log.info("当前车次下的车厢数：{}", carriageList.size());
//
//        // 循环生成每个车厢的座位
//        for (var trainCarriage : carriageList) {
//            // 拿到车厢数据：行数、座位类型(得到列数)
//            var rowCount = trainCarriage.getRowCount();
//            var seatType = trainCarriage.getSeatType();
//            int seatIndex = 1;
//            // 根据车厢的座位类型，筛选出所有的列，比如车箱类型是一等座，则筛选出columnList={ACDF}
//            var colEnumList = SeatColEnum.getColsByType(seatType);
//            log.info("根据车厢的座位类型，筛选出所有的列：{}", colEnumList);
//            // 循环行数
//            for (var row = 1; row <= rowCount; row++) {
//                // 循环列数
//                for (var seatColEnum : colEnumList) {
//                    // 构造座位数据并保存数据库
//                    var trainSeat = new TrainSeatEntity();
//                    trainSeat.setTrainCode(trainCode);
//                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
//                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
//                    trainSeat.setCol(seatColEnum.getCode());
//                    trainSeat.setSeatType(seatType);
//                    trainSeat.setCarriageSeatIndex(seatIndex++);
//                    trainSeat.setCreateTime(now);
//                    trainSeat.setUpdateTime(now);
//                    trainSeatMapper.insert(trainSeat);
//                }
//            }
//        }
//    }

    @Override
    public List<TrainSeat> selectByTrainCode(String trainCode) {
        var q = new HashMap<String,Object>();
        q.put("train_code",trainCode);
        return trainSeatMapper.selectByExample(q);
    }

}
