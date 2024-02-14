package com.jiawa.train.business.service;

import com.jiawa.train.business.entity.TrainCarriage;
import com.jiawa.train.business.req.TrainCarriageQueryReq;
import com.jiawa.train.business.req.TrainCarriageSaveReq;
import com.jiawa.train.business.resp.TrainCarriageQueryResp;
import com.jiawa.train.common.resp.PageResp;

import java.util.List;

public interface ITrainCarriageService {

    void save(TrainCarriageSaveReq req);
    PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req);

    void delete(Long id);

    List<TrainCarriage> selectByTrainCode(String trainCode);

}
