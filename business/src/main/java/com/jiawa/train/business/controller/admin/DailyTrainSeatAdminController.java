package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.DailyTrainSeatQueryReq;
import com.jiawa.train.business.req.DailyTrainSeatSaveReq;
import com.jiawa.train.business.resp.DailyTrainSeatQueryResp;
import com.jiawa.train.business.service.IDailyTrainSeatService;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/daily-train-seat")
@Slf4j
@RequiredArgsConstructor
public class DailyTrainSeatAdminController {

    private final IDailyTrainSeatService dailyTrainSeatService;

    @PostMapping("save")
    public void save(@Valid @RequestBody DailyTrainSeatSaveReq req) {
        dailyTrainSeatService.save(req);
    }

    @GetMapping("query-list")
    public PageResp<DailyTrainSeatQueryResp> queryList(@Valid DailyTrainSeatQueryReq req) {
        return dailyTrainSeatService.queryList(req);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        dailyTrainSeatService.delete(id);
    }

}
