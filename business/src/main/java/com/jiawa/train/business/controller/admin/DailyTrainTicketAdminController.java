package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.DailyTrainTicketQueryReq;
import com.jiawa.train.business.req.DailyTrainTicketSaveReq;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import com.jiawa.train.business.service.IDailyTrainTicketService;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {

    private final IDailyTrainTicketService dailyTrainTicketService;

    @PostMapping("save")
    public void save(@Valid @RequestBody DailyTrainTicketSaveReq req) {
        dailyTrainTicketService.save(req);
    }

    @GetMapping("query-list")
    public PageResp<DailyTrainTicketQueryResp> queryList(@Valid DailyTrainTicketQueryReq req) {
        return dailyTrainTicketService.queryList(req);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        dailyTrainTicketService.delete(id);
    }

}
