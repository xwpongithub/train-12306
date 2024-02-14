package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.ConfirmOrderQueryReq;
import com.jiawa.train.business.resp.ConfirmOrderQueryResp;
import com.jiawa.train.business.service.IConfirmOrderService;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/confirm-order")
public class ConfirmOrderAdminController {

    private final IConfirmOrderService confirmOrderService;

    @GetMapping("query-list")
    public PageResp<ConfirmOrderQueryResp> queryList(@Valid ConfirmOrderQueryReq req) {
        return confirmOrderService.queryList(req);
    }

}
