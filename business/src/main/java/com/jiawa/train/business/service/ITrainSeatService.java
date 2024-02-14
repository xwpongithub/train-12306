package com.jiawa.train.business.service;

import com.jiawa.train.business.entity.TrainSeat;
import com.jiawa.train.business.req.TrainSeatQueryReq;
import com.jiawa.train.business.req.TrainSeatSaveReq;
import com.jiawa.train.business.resp.TrainSeatQueryResp;
import com.jiawa.train.common.resp.PageResp;

import java.util.List;

public interface ITrainSeatService {

    void save(TrainSeatSaveReq req);

    PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req);

    void delete(Long id);

//    void genTrainSeat(String trainCode);
    List<TrainSeat> selectByTrainCode(String trainCode);

}
