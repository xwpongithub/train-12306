package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.service.ISkTokenService;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.business.req.SkTokenQueryReq;
import com.jiawa.train.business.req.SkTokenSaveReq;
import com.jiawa.train.business.resp.SkTokenQueryResp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/sk-token")
public class SkTokenAdminController {

    private final ISkTokenService skTokenService;

    @PostMapping("/save")
    public void save(@Valid @RequestBody SkTokenSaveReq req) {
        skTokenService.save(req);
    }

    @GetMapping("/query-list")
    public PageResp<SkTokenQueryResp> queryList(@Valid SkTokenQueryReq req) {
       return skTokenService.queryList(req);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        skTokenService.delete(id);
    }

}
