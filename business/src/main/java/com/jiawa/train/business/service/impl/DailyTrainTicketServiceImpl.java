package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.jiawa.train.business.entity.DailyTrainTicket;
import com.jiawa.train.business.mapper.DailyTrainTicketMapper;
import com.jiawa.train.business.req.DailyTrainTicketQueryReq;
import com.jiawa.train.business.req.DailyTrainTicketSaveReq;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import com.jiawa.train.business.service.IDailyTrainTicketService;
import com.jiawa.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DailyTrainTicketServiceImpl implements IDailyTrainTicketService {

    private final DailyTrainTicketMapper dailyTrainTicketMapper;

    @Override
    @Deprecated
    public void save(DailyTrainTicketSaveReq req) {
        var now = DateUtil.date();
        var dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
        if (Objects.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insertSelective(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            var q = new HashMap<String,Object>();
            q.put("id",req.getId());
            dailyTrainTicketMapper.updateSelective(dailyTrainTicket,q);
        }
    }

    @Override
    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
        var dailyTrainTicketList =dailyTrainTicketMapper.page(req.getPage(), req.getSize(),req.getDate(),req.getTrainCode(),req.getStart(),req.getEndVal());
        var resp = new PageResp<DailyTrainTicketQueryResp>();
        var list = BeanUtil.copyToList(dailyTrainTicketList , DailyTrainTicketQueryResp.class);
        resp.setTotal(dailyTrainTicketList.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }

    @Override
    public DailyTrainTicket selectByUnique(Date date, String trainCode, String start, String end) {
        var q = new HashMap<String,Object>();
        q.put("date",date);
        q.put("train_code",trainCode);
        q.put("start",start);
        q.put("end_val",end);
        return dailyTrainTicketMapper.selectOne(q);
    }

}
