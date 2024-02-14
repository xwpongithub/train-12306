package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.StationQueryReq;
import com.jiawa.train.business.req.StationSaveReq;
import com.jiawa.train.business.resp.StationQueryResp;
import com.jiawa.train.business.service.IStationService;
import com.jiawa.train.common.resp.PageResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/station")
@RequiredArgsConstructor
public class StationAdminController {

    private final IStationService stationService;

    @PostMapping("save")
    public void save(@Valid @RequestBody StationSaveReq req) {
        stationService.save(req);
    }

    @GetMapping("query-list")
    public PageResp<StationQueryResp> queryList(@Valid StationQueryReq req) {
        return stationService.queryList(req);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        stationService.delete(id);
    }

    @GetMapping("query-all")
    public List<StationQueryResp> queryList() {
        return stationService.queryAll();
    }

}
