package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
            dailyTrainTicketMapper.insert(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateById(dailyTrainTicket);
        }
    }

    @Override
    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
        // 常见的缓存过期策略
        // TTL 超时时间
        // LRU 最近最少使用
        // LFU 最近最不经常使用
        // FIFO 先进先出
        // Random 随机淘汰策略
        // 去缓存里取数据，因数据库本身就没数据而造成缓存穿透
        // if (有数据) { null []
        //     return
        // } else {
        //     去数据库取数据
        // }
        var q = Wrappers.<DailyTrainTicket>lambdaQuery();
        q.orderByDesc(DailyTrainTicket::getDate)
                .orderByAsc(DailyTrainTicket::getStartTime,
                        DailyTrainTicket::getTrainCode,
                        DailyTrainTicket::getStartIndex,
                        DailyTrainTicket::getEndIndex)
                .eq(Objects.nonNull(req.getDate()),DailyTrainTicket::getDate,req.getDate())
                .eq(StrUtil.isNotBlank(req.getTrainCode()),DailyTrainTicket::getDate,req.getTrainCode())
                .like(StrUtil.isNotBlank(req.getStart()),DailyTrainTicket::getStart,req.getStart())
                .like(StrUtil.isNotBlank(req.getEndVal()),DailyTrainTicket::getEndVal,req.getEndVal());
        var p = new Page<DailyTrainTicket>(req.getPage(),req.getSize());
        var dbPage =dailyTrainTicketMapper.selectPage(p,q);
        var resp = new PageResp<DailyTrainTicketQueryResp>();
        var list = BeanUtil.copyToList(dbPage.getRecords() , DailyTrainTicketQueryResp.class);
        resp.setTotal((int)dbPage.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainTicketMapper.deleteById(id);
    }

    @Override
    public DailyTrainTicket selectByUnique(Date date, String trainCode, String start, String end) {
        var q = Wrappers.<DailyTrainTicket>lambdaQuery();
        q.eq(DailyTrainTicket::getDate,date)
                .eq(DailyTrainTicket::getTrainCode,trainCode)
                .eq(DailyTrainTicket::getStart,start)
                .eq(DailyTrainTicket::getEndVal,end);
        return dailyTrainTicketMapper.selectOne(q);
    }

}
