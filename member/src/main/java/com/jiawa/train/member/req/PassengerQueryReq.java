package com.jiawa.train.member.req;

import com.jiawa.train.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PassengerQueryReq extends PageReq {

    private Long memberId;

}
