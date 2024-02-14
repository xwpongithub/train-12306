package com.jiawa.train.business.service;

import com.jiawa.train.business.req.StationQueryReq;
import com.jiawa.train.business.req.StationSaveReq;
import com.jiawa.train.business.resp.StationQueryResp;
import com.jiawa.train.common.resp.PageResp;

import java.util.List;

public interface IStationService {

    void save(StationSaveReq req);

    void delete(Long id);

    PageResp<StationQueryResp> queryList(StationQueryReq req);

    List<StationQueryResp> queryAll();

}
