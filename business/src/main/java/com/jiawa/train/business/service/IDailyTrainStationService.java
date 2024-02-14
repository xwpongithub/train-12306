package com.jiawa.train.business.service;

import com.jiawa.train.business.req.DailyTrainStationQueryReq;
import com.jiawa.train.business.req.DailyTrainStationSaveReq;
import com.jiawa.train.business.resp.DailyTrainStationQueryResp;
import com.jiawa.train.common.resp.PageResp;

import java.util.Date;
import java.util.List;

public interface IDailyTrainStationService {

    void save(DailyTrainStationSaveReq req);

    PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req);

    void delete(Long id);

    void genDaily(Date date, String trainCode);

    long countByTrainCode(Date date, String trainCode);

    List<DailyTrainStationQueryResp> queryByTrain(Date date, String trainCode);

}
