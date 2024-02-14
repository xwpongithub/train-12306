package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.DailyTrainCarriageQueryReq;
import com.jiawa.train.business.req.DailyTrainCarriageSaveReq;
import com.jiawa.train.business.resp.DailyTrainCarriageQueryResp;
import com.jiawa.train.business.service.IDailyTrainCarriageService;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("admin/daily-train-carriage")
public class DailyTrainCarriageAdminController {

    private final IDailyTrainCarriageService dailyTrainCarriageService;

    @PostMapping("save")
    public void save(@Valid @RequestBody DailyTrainCarriageSaveReq req) {
        dailyTrainCarriageService.save(req);
    }

    @GetMapping("query-list")
    public PageResp<DailyTrainCarriageQueryResp> queryList(@Valid DailyTrainCarriageQueryReq req) {
        return dailyTrainCarriageService.queryList(req);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        dailyTrainCarriageService.delete(id);
    }

}
