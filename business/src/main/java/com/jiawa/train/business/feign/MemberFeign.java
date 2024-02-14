package com.jiawa.train.business.feign;

import com.jiawa.train.common.JsonResult;
import com.jiawa.train.common.req.MemberTicketReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "member", url = "http://127.0.0.1:8001")
public interface MemberFeign {

   @GetMapping("/member/feign/ticket/save")
   JsonResult save(@RequestBody MemberTicketReq req);

}
