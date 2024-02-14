package com.jiawa.train.business.service;

import com.jiawa.train.business.entity.DailyTrainTicket;
import com.jiawa.train.business.req.DailyTrainTicketQueryReq;
import com.jiawa.train.business.req.DailyTrainTicketSaveReq;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import com.jiawa.train.common.resp.PageResp;

import java.util.Date;

public interface IDailyTrainTicketService {

    void save(DailyTrainTicketSaveReq req);

    void delete(Long id);

    PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req);

    DailyTrainTicket selectByUnique(Date date, String trainCode, String start, String end);

}
