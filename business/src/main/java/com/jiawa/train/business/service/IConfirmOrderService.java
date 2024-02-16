package com.jiawa.train.business.service;

import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.req.ConfirmOrderQueryReq;
import com.jiawa.train.business.resp.ConfirmOrderQueryResp;
import com.jiawa.train.common.resp.PageResp;

import java.sql.SQLException;

public interface IConfirmOrderService {

    PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req);
    void doConfirmOrder(ConfirmOrderDoReq req) throws Exception;
}
