package com.jiawa.train.business.service;

import cn.hutool.db.Session;
import com.jiawa.train.business.entity.DailyTrainSeat;
import com.jiawa.train.business.entity.DailyTrainTicket;
import com.jiawa.train.business.req.ConfirmOrderTicketReq;

import java.sql.SQLException;
import java.util.List;

public interface IAfterConfirmOrderService {

    void afterDoConfirm(
            DailyTrainTicket dailyTrainTicket,
            List<DailyTrainSeat> finalSeatList,
            List<ConfirmOrderTicketReq> tickets,
            Session session) throws SQLException;

}
