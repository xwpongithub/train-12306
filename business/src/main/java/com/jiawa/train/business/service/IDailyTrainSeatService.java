package com.jiawa.train.business.service;

import com.jiawa.train.business.entity.DailyTrainSeat;
import com.jiawa.train.business.req.DailyTrainSeatQueryReq;
import com.jiawa.train.business.req.DailyTrainSeatSaveReq;
import com.jiawa.train.business.req.SeatSellReq;
import com.jiawa.train.business.resp.DailyTrainSeatQueryResp;
import com.jiawa.train.business.resp.SeatSellResp;
import com.jiawa.train.common.resp.PageResp;

import java.util.Date;
import java.util.List;

public interface IDailyTrainSeatService {

    void save(DailyTrainSeatSaveReq req);

    PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req);

    void delete(Long id);

    int countSeat(Date date, String trainCode);

    int countSeat(Date date, String trainCode, String seatType);

    List<DailyTrainSeat> selectByCarriage(Date date, String trainCode, Integer carriageIndex);

    List<SeatSellResp> querySeatSell(SeatSellReq req);

}
