package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.jiawa.train.business.entity.TrainCarriage;
import com.jiawa.train.business.enums.SeatColEnum;
import com.jiawa.train.business.mapper.TrainCarriageMapper;
import com.jiawa.train.business.req.TrainCarriageQueryReq;
import com.jiawa.train.business.req.TrainCarriageSaveReq;
import com.jiawa.train.business.resp.TrainCarriageQueryResp;
import com.jiawa.train.business.service.ITrainCarriageService;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class TrainCarriageServiceImpl implements ITrainCarriageService {

    private final TrainCarriageMapper trainCarriageMapper;

    @Override
    public void save(TrainCarriageSaveReq req) {
        var now = DateUtil.date();
        // 自动计算出列数和总座位数
        var seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount() * req.getRowCount());
        var trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        if (Objects.isNull(trainCarriage.getId())) {
            // 保存之前，先校验唯一键是否存在
            var trainCarriageDB = selectByUnique(req.getTrainCode(), req.getIndex());
            if (Objects.nonNull(trainCarriageDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR);
            }
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insertSelective(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            var q = new HashMap<String,Object>();
            q.put("id", req.getId());
            trainCarriageMapper.updateSelective(trainCarriage,q);
        }
    }

    @Override
    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req) {
        var trainCarriageList = trainCarriageMapper.page(req.getPage(), req.getSize(),req.getTrainCode());
        var resp = new PageResp<TrainCarriageQueryResp>();
        var list = BeanUtil.copyToList(trainCarriageList , TrainCarriageQueryResp.class);
        resp.setTotal(trainCarriageList.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        trainCarriageMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<TrainCarriage> selectByTrainCode(String trainCode) {
        var q = new HashMap<String,Object>();
        q.put("train_code",trainCode);
        return trainCarriageMapper.selectByExample(q);
    }

    private TrainCarriage selectByUnique(String trainCode, Integer index) {
        var q = new HashMap<String,Object>();
        q.put("train_code",trainCode);
        q.put("index",index);
        return trainCarriageMapper.selectOne(q);
    }

}
