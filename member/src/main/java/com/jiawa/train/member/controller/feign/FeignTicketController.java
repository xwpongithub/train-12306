package com.jiawa.train.member.controller.feign;

import com.jiawa.train.common.annotation.IgnoreResponseSerializable;
import com.jiawa.train.member.service.ITicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jiawa.train.common.req.MemberTicketReq;

@RestController
@RequestMapping("/feign/ticket")
@RequiredArgsConstructor
public class FeignTicketController {

    private final ITicketService ticketService;

    @PostMapping("/save")
    @IgnoreResponseSerializable
    public void save(@Valid @RequestBody MemberTicketReq req) throws Exception {
        ticketService.save(req);
    }

}
