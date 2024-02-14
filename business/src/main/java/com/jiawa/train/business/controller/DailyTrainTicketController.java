package com.jiawa.train.business.controller;

import com.jiawa.train.business.req.DailyTrainTicketQueryReq;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import com.jiawa.train.business.service.IDailyTrainTicketService;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("daily-train-ticket")
@RequiredArgsConstructor
public class DailyTrainTicketController {

    private final IDailyTrainTicketService dailyTrainTicketService;

    @GetMapping("/query-list")
    public PageResp<DailyTrainTicketQueryResp> queryList(@Valid DailyTrainTicketQueryReq req) {
        return dailyTrainTicketService.queryList(req);
    }

}
