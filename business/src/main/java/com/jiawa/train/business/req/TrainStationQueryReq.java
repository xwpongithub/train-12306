package com.jiawa.train.business.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.jiawa.train.common.req.PageReq;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrainStationQueryReq extends PageReq {

    private String trainCode;

}
