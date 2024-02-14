package com.jiawa.train.member.service;

import com.jiawa.train.member.req.PassengerQueryReq;
import com.jiawa.train.member.resp.PassengerQueryResp;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.member.req.PassengerSaveReq;

import java.util.List;

public interface IPassengerService {

    void save(PassengerSaveReq req);

    PageResp<PassengerQueryResp> queryList(PassengerQueryReq req);

    void delete(Long id);

    List<PassengerQueryResp> queryMine();

}
