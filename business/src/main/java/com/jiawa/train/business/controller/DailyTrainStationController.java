package com.jiawa.train.business.controller;

import com.jiawa.train.business.req.DailyTrainStationQueryAllReq;
import com.jiawa.train.business.resp.DailyTrainStationQueryResp;
import com.jiawa.train.business.service.IDailyTrainStationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/daily-train-station")
@RequiredArgsConstructor
public class DailyTrainStationController {

    private final IDailyTrainStationService dailyTrainStationService;

    @GetMapping("/query-by-train-code")
    public List<DailyTrainStationQueryResp> queryByTrain(@Valid DailyTrainStationQueryAllReq req) {
        return dailyTrainStationService.queryByTrain(req.getDate(), req.getTrainCode());
    }

}
