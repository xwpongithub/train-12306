package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Override
    public void save(TrainSeatSaveReq req) {
        var now = DateUtil.date();
        var trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if (Objects.isNull(trainSeat.getId())) {
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateById(trainSeat);
        }
    }

    @Override
    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
        var q = Wrappers.<TrainSeat>lambdaQuery();
        q.eq(StrUtil.isNotBlank(req.getTrainCode()),TrainSeat::getTrainCode,req.getTrainCode())
                .orderByAsc(
                        TrainSeat::getTrainCode,
                        TrainSeat::getCarriageIndex,
                        TrainSeat::getCarriageSeatIndex
                );
        var p = new Page<TrainSeat>(req.getPage(),req.getSize());
        var dbPage = trainSeatMapper.selectPage(p,q);
        var resp = new PageResp<TrainSeatQueryResp>();
        var list = BeanUtil.copyToList(dbPage.getRecords() , TrainSeatQueryResp.class);
        resp.setTotal((int)dbPage.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        trainSeatMapper.deleteById(id);
    }

    @Override
    public List<TrainSeat> selectByTrainCode(String trainCode) {
        var q = Wrappers.<TrainSeat>lambdaQuery();
        q.orderByAsc(TrainSeat::getId)
                .eq(TrainSeat::getTrainCode,trainCode);
        return trainSeatMapper.selectList(q);
    }

}
