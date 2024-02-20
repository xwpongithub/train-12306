package com.jiawa.train.business.service;

import com.jiawa.train.business.req.SkTokenQueryReq;
import com.jiawa.train.business.req.SkTokenSaveReq;
import com.jiawa.train.business.resp.SkTokenQueryResp;
import com.jiawa.train.common.resp.PageResp;

import java.util.Date;

public interface ISkTokenService {

    void save(SkTokenSaveReq req);
    void delete(Long id);
    PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req);
    void genDaily(Date date, String trainCode);

    boolean validSkToken(Date date, String trainCode, Long id);
}
