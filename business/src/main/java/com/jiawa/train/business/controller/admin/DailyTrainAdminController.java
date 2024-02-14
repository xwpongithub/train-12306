package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.DailyTrainQueryReq;
import com.jiawa.train.business.req.DailyTrainSaveReq;
import com.jiawa.train.business.resp.DailyTrainQueryResp;
import com.jiawa.train.business.service.IDailyTrainService;
import com.jiawa.train.common.JsonResult;
import com.jiawa.train.common.annotation.IgnoreResponseSerializable;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("admin/daily-train")
@RequiredArgsConstructor
public class DailyTrainAdminController {

    private final IDailyTrainService dailyTrainService;

    @PostMapping("save")
    public void save(@Valid @RequestBody DailyTrainSaveReq req) {
        dailyTrainService.save(req);
    }

    @GetMapping("query-list")
    public PageResp<DailyTrainQueryResp> queryList(@Valid DailyTrainQueryReq req) {
        return dailyTrainService.queryList(req);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        dailyTrainService.delete(id);
    }

    @GetMapping("gen-daily/{date}")
    @IgnoreResponseSerializable
    public JsonResult genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        dailyTrainService.genDaily(date);
        return JsonResult.ok("批量生成车次数据成功");
    }

}
