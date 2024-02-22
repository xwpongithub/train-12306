package com.jiawa.train.business.service;

import com.jiawa.train.business.req.ConfirmOrderDoReq;

public interface IBeforeConfirmOrderService {

    Long beforeDoConfirmOrder(ConfirmOrderDoReq req);

    Integer queryLineCount(Long id);

    Integer cancel(Long id);
}
