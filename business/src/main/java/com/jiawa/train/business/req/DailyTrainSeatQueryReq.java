package com.jiawa.train.business.req;

import com.jiawa.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DailyTrainSeatQueryReq extends PageReq {

    private String trainCode;

}
