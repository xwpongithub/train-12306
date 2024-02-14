package com.jiawa.train.business.controller;

import com.jiawa.train.business.resp.StationQueryResp;
import com.jiawa.train.business.service.IStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("station")
public class StationController {

    private final IStationService stationService;

    @GetMapping("query-all")
    public List<StationQueryResp> queryList() {
        return stationService.queryAll();
    }

}
