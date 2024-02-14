package com.jiawa.train.member.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.jiawa.train.common.req.PageReq;
@Data
@EqualsAndHashCode(callSuper = true)
public class TicketQueryReq extends PageReq {

    private Long memberId;

}
