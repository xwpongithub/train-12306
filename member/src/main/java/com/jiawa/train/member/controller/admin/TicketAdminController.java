package com.jiawa.train.member.controller.admin;

import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.member.req.TicketQueryReq;
import com.jiawa.train.member.resp.TicketQueryResp;
import com.jiawa.train.member.service.ITicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/ticket")
@RequiredArgsConstructor
public class TicketAdminController {

    private final ITicketService ticketService;

    @GetMapping("/query-list")
    public PageResp<TicketQueryResp> queryList(@Valid TicketQueryReq req) {
        return ticketService.queryList(req);
    }

}
