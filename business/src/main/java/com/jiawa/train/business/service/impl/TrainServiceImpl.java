package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.jiawa.train.business.entity.Train;
import com.jiawa.train.business.mapper.TrainMapper;
import com.jiawa.train.business.req.TrainQueryReq;
import com.jiawa.train.business.req.TrainSaveReq;
import com.jiawa.train.business.resp.TrainQueryResp;
import com.jiawa.train.business.service.ITrainService;
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
public class TrainServiceImpl implements ITrainService {

    private final TrainMapper trainMapper;

    @Override
    public void save(TrainSaveReq req) {
        var now = DateUtil.date();
        var train = BeanUtil.copyProperties(req, Train.class);
        if (Objects.isNull(train.getId())) {
            // 保存之前，先校验唯一键是否存在
            var trainDB = selectByUnique(req.getCode());
            if (Objects.nonNull(trainDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insertSelective(train);
        } else {
            train.setUpdateTime(now);
            var q = new HashMap<String,Object>();
            q.put("id", train.getId());
            trainMapper.updateSelective(train,q);
        }
    }

    @Override
    public PageResp<TrainQueryResp> queryList(TrainQueryReq req) {
        var trainList = trainMapper.page(req.getPage(), req.getSize());
        var resp = new PageResp<TrainQueryResp>();
        var list = BeanUtil.copyToList(trainList , TrainQueryResp.class);
        resp.setTotal(trainList.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        trainMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<TrainQueryResp> queryAll() {
        var trainList = selectAll();
        // LOG.info("再查一次");
        // trainList = selectAll();
        return BeanUtil.copyToList(trainList, TrainQueryResp.class);
    }

    @Override
    public List<Train> selectAll() {
        return trainMapper.selectByExample(null);
    }

    private Train selectByUnique(String code) {
        var q = new HashMap<String,Object>();
        q.put("code",code);
        return trainMapper.selectOne(q);
    }

}
