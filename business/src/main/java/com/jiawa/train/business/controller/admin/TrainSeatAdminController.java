package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.TrainSeatQueryReq;
import com.jiawa.train.business.req.TrainSeatSaveReq;
import com.jiawa.train.business.resp.TrainSeatQueryResp;
import com.jiawa.train.business.service.ITrainSeatService;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/train-seat")
@RequiredArgsConstructor
public class TrainSeatAdminController {

    private final ITrainSeatService trainSeatService;

    @PostMapping("save")
    public void save(@Valid @RequestBody TrainSeatSaveReq req) {
        trainSeatService.save(req);
    }

    @GetMapping("query-list")
    public PageResp<TrainSeatQueryResp> queryList(@Valid TrainSeatQueryReq req) {
        return trainSeatService.queryList(req);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        trainSeatService.delete(id);
    }

}
