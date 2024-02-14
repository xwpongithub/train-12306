package com.jiawa.train.business.service;

import com.jiawa.train.business.entity.Train;
import com.jiawa.train.business.req.TrainQueryReq;
import com.jiawa.train.business.req.TrainSaveReq;
import com.jiawa.train.business.resp.TrainQueryResp;
import com.jiawa.train.common.resp.PageResp;

import java.util.List;

public interface ITrainService {

    void save(TrainSaveReq req);

    PageResp<TrainQueryResp> queryList(TrainQueryReq req);

    void delete(Long id);

    List<TrainQueryResp> queryAll();

    List<Train> selectAll();

}
