package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.TrainStationQueryReq;
import com.jiawa.train.business.req.TrainStationSaveReq;
import com.jiawa.train.business.resp.TrainStationQueryResp;
import com.jiawa.train.business.service.ITrainStationService;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/train-station")
@RequiredArgsConstructor
public class TrainStationAdminController {

    private final ITrainStationService trainStationService;

    @PostMapping("save")
    public void save(@Valid @RequestBody TrainStationSaveReq req) {
        trainStationService.save(req);
    }

    @GetMapping("query-list")
    public PageResp<TrainStationQueryResp> queryList(@Valid TrainStationQueryReq req) {
        return trainStationService.queryList(req);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        trainStationService.delete(id);
    }

}
