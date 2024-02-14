package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.jiawa.train.business.entity.DailyTrainCarriage;
import com.jiawa.train.business.enums.SeatColEnum;
import com.jiawa.train.business.mapper.DailyTrainCarriageMapper;
import com.jiawa.train.business.req.DailyTrainCarriageQueryReq;
import com.jiawa.train.business.req.DailyTrainCarriageSaveReq;
import com.jiawa.train.business.resp.DailyTrainCarriageQueryResp;
import com.jiawa.train.business.service.IDailyTrainCarriageService;
import com.jiawa.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DailyTrainCarriageServiceImpl implements IDailyTrainCarriageService {

    private final DailyTrainCarriageMapper dailyTrainCarriageMapper;

//    private final TrainCarriageService trainCarriageService;

    @Override
    public void save(DailyTrainCarriageSaveReq req) {
        var now = DateUtil.date();
        // 自动计算出列数和总座位数
        var seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount() * req.getRowCount());

        var dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);
        if (Objects.isNull(dailyTrainCarriage.getId())) {
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.insertSelective(dailyTrainCarriage);
        } else {
            dailyTrainCarriage.setUpdateTime(now);
            var q = new HashMap<String,Object>();
            q.put("id", req.getId());
            dailyTrainCarriageMapper.updateSelective(dailyTrainCarriage,q);
        }
    }

    @Override
    public PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req) {
        var trainList = dailyTrainCarriageMapper.page(req.getPage(), req.getSize(),req.getDate(),req.getTrainCode());
        var resp = new PageResp<DailyTrainCarriageQueryResp>();
        var list = BeanUtil.copyToList(trainList , DailyTrainCarriageQueryResp.class);
        resp.setTotal(trainList.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainCarriageMapper.deleteByPrimaryKey(id);
    }

//    @Transactional(rollbackFor = Exception.class)
    @Override
    public void genDaily(Date date, String trainCode) {
//        log.info("生成日期【{}】车次【{}】的车厢信息开始", DateUtil.formatDate(date), trainCode);
//
//        // 查出某车次的所有的车厢信息
//        var carriageList = trainCarriageService.selectByTrainCode(trainCode);
//        if (CollUtil.isEmpty(carriageList)) {
//            log.info("该车次没有车厢基础数据，生成该车次的车厢信息结束");
//            return;
//        }
//
//        for (var trainCarriage : carriageList) {
//            var now = DateUtil.date();
//            var dailyTrainCarriage = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriageEntity.class);
//            dailyTrainCarriage.setCreateTime(now);
//            dailyTrainCarriage.setUpdateTime(now);
//            dailyTrainCarriage.setDate(date);
//            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
//        }
//        log.info("生成日期【{}】车次【{}】的车厢信息结束", DateUtil.formatDate(date), trainCode);
    }

    @Override
    public List<DailyTrainCarriage> selectBySeatType(Date date, String trainCode, String seatType) {
        var q = new HashMap<String,Object>();
        q.put("date",date);
        q.put("train_code",trainCode);
        q.put("seat_type",seatType);
        return dailyTrainCarriageMapper.selectByExample(q);
    }


}
