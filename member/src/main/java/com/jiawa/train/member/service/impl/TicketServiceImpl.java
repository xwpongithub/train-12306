package com.jiawa.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiawa.train.common.req.MemberTicketReq;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.member.entity.Ticket;
import com.jiawa.train.member.mapper.TicketMapper;
import com.jiawa.train.member.req.TicketQueryReq;
import com.jiawa.train.member.resp.TicketQueryResp;
import com.jiawa.train.member.service.ITicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements ITicketService {

    private final TicketMapper ticketMapper;

    @Override
    public void save(MemberTicketReq req) {
        var now = DateUtil.date();
        var ticket = BeanUtil.copyProperties(req, Ticket.class);
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);
        ticketMapper.insert(ticket);
    }

    @Override
    public PageResp<TicketQueryResp> queryList(TicketQueryReq req) {
        var memberId = req.getMemberId();
        var page = new Page<Ticket>(req.getPage(), req.getSize());
        var q = Wrappers.<Ticket>lambdaQuery();
        q.orderByDesc(Ticket::getId)
                .eq(Objects.nonNull(memberId),
                        Ticket::getMemberId, memberId);
        var pageData = ticketMapper.selectPage(page,q);
        var list = BeanUtil.copyToList(pageData.getRecords(), TicketQueryResp.class);
        PageResp<TicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal((int)pageData.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

}
