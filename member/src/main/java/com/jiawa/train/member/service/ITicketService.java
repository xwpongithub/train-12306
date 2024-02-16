package com.jiawa.train.member.service;

import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.req.MemberTicketReq;
import com.jiawa.train.member.req.TicketQueryReq;
import com.jiawa.train.member.resp.TicketQueryResp;

public interface ITicketService {


    void save(MemberTicketReq req) throws Exception;

    PageResp<TicketQueryResp> queryList(TicketQueryReq req);

}
