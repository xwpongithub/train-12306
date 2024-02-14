package com.jiawa.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.member.entity.Ticket;
import com.jiawa.train.member.mapper.TicketMapper;
import com.jiawa.train.common.req.MemberTicketReq;
import com.jiawa.train.member.req.TicketQueryReq;
import com.jiawa.train.member.resp.TicketQueryResp;
import com.jiawa.train.member.service.ITicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        ticketMapper.insertSelective(ticket);
    }

    @Override
    public PageResp<TicketQueryResp> queryList(TicketQueryReq req) {
        var ticketList = ticketMapper.page(req.getPage(),req.getSize(),req.getMemberId());
        var list = BeanUtil.copyToList(ticketList, TicketQueryResp.class);
        PageResp<TicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(ticketList.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

}
