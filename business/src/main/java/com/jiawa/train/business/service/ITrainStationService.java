package com.jiawa.train.business.service;

import com.jiawa.train.business.entity.TrainStation;
import com.jiawa.train.business.req.TrainStationQueryReq;
import com.jiawa.train.business.req.TrainStationSaveReq;
import com.jiawa.train.business.resp.TrainStationQueryResp;
import com.jiawa.train.common.resp.PageResp;

import java.util.List;

public interface ITrainStationService {

    void save(TrainStationSaveReq req);
    void delete(Long id);
    List<TrainStation> selectByTrainCode(String trainCode);

    PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req);

}
