package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.TrainQueryReq;
import com.jiawa.train.business.req.TrainSaveReq;
import com.jiawa.train.business.resp.TrainQueryResp;
import com.jiawa.train.business.service.ITrainService;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/train")
public class TrainAdminController {

    private final ITrainService trainService;
//    private final TrainSeatService trainSeatService;

    @PostMapping("save")
    public void save(@Valid @RequestBody TrainSaveReq req) {
        trainService.save(req);
    }

    @GetMapping("query-list")
    public PageResp<TrainQueryResp> queryList(@Valid TrainQueryReq req) {
        return trainService.queryList(req);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        trainService.delete(id);
    }

    @GetMapping("query-all")
    public List<TrainQueryResp> queryList() {
        return trainService.queryAll();
    }

//    @GetMapping("gen-seat/{trainCode}")
//    public CommonResp<Object> genSeat(@PathVariable String trainCode) {
//        trainSeatService.genTrainSeat(trainCode);
//        return new CommonResp<>();
//    }

}
