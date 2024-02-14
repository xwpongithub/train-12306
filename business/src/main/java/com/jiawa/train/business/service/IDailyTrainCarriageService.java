package com.jiawa.train.business.service;

import com.jiawa.train.business.req.DailyTrainCarriageSaveReq;
import com.jiawa.train.business.entity.DailyTrainCarriage;
import com.jiawa.train.business.req.DailyTrainCarriageQueryReq;
import com.jiawa.train.business.resp.DailyTrainCarriageQueryResp;
import com.jiawa.train.common.resp.PageResp;

import java.util.Date;
import java.util.List;

public interface IDailyTrainCarriageService {

    void save(DailyTrainCarriageSaveReq req);

    PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req);
    void delete(Long id);
    void genDaily(Date date, String trainCode);
    List<DailyTrainCarriage> selectBySeatType(Date date, String trainCode, String seatType);

}
