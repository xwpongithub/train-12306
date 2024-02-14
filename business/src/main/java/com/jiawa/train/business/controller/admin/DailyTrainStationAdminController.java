package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.DailyTrainStationQueryReq;
import com.jiawa.train.business.req.DailyTrainStationSaveReq;
import com.jiawa.train.business.resp.DailyTrainStationQueryResp;
import com.jiawa.train.business.service.IDailyTrainStationService;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/daily-train-station")
@RequiredArgsConstructor
public class DailyTrainStationAdminController {

    private final IDailyTrainStationService dailyTrainStationService;

    @PostMapping("save")
    public void save(@Valid @RequestBody DailyTrainStationSaveReq req) {
        dailyTrainStationService.save(req);
    }

    @GetMapping("query-list")
    public PageResp<DailyTrainStationQueryResp> queryList(@Valid DailyTrainStationQueryReq req) {
        return dailyTrainStationService.queryList(req);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        dailyTrainStationService.delete(id);
    }

}
