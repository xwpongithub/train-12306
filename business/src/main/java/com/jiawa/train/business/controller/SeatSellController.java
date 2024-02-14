package com.jiawa.train.business.controller;

import com.jiawa.train.business.req.SeatSellReq;
import com.jiawa.train.business.resp.SeatSellResp;
import com.jiawa.train.business.service.IDailyTrainSeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seat-sell")
public class SeatSellController {

    private final IDailyTrainSeatService dailyTrainSeatService;

    @GetMapping("/query")
    public List<SeatSellResp> query(@Valid SeatSellReq req) {
       return dailyTrainSeatService.querySeatSell(req);
    }

}
