package com.jiawa.train.member.controller;

import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.member.req.PassengerQueryReq;
import com.jiawa.train.member.req.PassengerSaveReq;
import com.jiawa.train.member.resp.PassengerQueryResp;
import com.jiawa.train.member.service.IPassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("passenger")
public class PassengerController {

    private final IPassengerService passengerService;

    @PostMapping("save")
    public void save(@Valid @RequestBody PassengerSaveReq req) {
        passengerService.save(req);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        passengerService.delete(id);
    }

    @GetMapping("query-list")
    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq req) {
        if (Objects.isNull(req.getMemberId())) {
            req.setMemberId(LoginMemberContext.getId());
        }
        return passengerService.queryList(req);
    }

    @GetMapping("query-mine")
    public List<PassengerQueryResp> queryMine() {
        return passengerService.queryMine();
    }

}
