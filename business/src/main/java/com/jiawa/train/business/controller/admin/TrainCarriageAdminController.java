package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.TrainCarriageQueryReq;
import com.jiawa.train.business.req.TrainCarriageSaveReq;
import com.jiawa.train.business.resp.TrainCarriageQueryResp;
import com.jiawa.train.business.service.ITrainCarriageService;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/train-carriage")
public class TrainCarriageAdminController {

    private final ITrainCarriageService trainCarriageService;

    @PostMapping("save")
    public void save(@Valid @RequestBody TrainCarriageSaveReq req) {
        trainCarriageService.save(req);
    }

    @GetMapping("query-list")
    public PageResp<TrainCarriageQueryResp> queryList(@Valid TrainCarriageQueryReq req) {
        return trainCarriageService.queryList(req);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        trainCarriageService.delete(id);
    }

}
