package com.jiawa.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.member.entity.Passenger;
import com.jiawa.train.member.mapper.PassengerMapper;
import com.jiawa.train.member.req.PassengerQueryReq;
import com.jiawa.train.member.req.PassengerSaveReq;
import com.jiawa.train.member.resp.PassengerQueryResp;
import com.jiawa.train.member.service.IPassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements IPassengerService {

    private final PassengerMapper passengerMapper;
    @Override
    public void save(PassengerSaveReq req) {
        var now = DateUtil.date();
        var passenger = BeanUtil.copyProperties(req, Passenger.class);
        passenger.setMemberId(LoginMemberContext.getId());
        var id = req.getId();
        if (Objects.isNull(id)) {
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passengerMapper.insert(passenger);
        } else {
            passenger.setUpdateTime(now);
            passengerMapper.updateById(passenger);
        }
    }

    @Override
    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq req) {

        var q = Wrappers.<Passenger>lambdaQuery();
        var memberId = req.getMemberId();
        q.eq(Objects.nonNull(memberId),Passenger::getMemberId,memberId)
                .orderByAsc(Passenger::getId);
        var page = new Page<Passenger>(req.getPage(), req.getSize());
        var pageData = passengerMapper.selectPage(page,q );
        var resp = new PageResp<PassengerQueryResp>();
        var list = BeanUtil.copyToList(pageData.getRecords(), PassengerQueryResp.class);
        resp.setTotal((int)pageData.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        passengerMapper.deleteById(id);
    }

    @Override
    public List<PassengerQueryResp> queryMine() {
        var q = Wrappers.<Passenger>lambdaQuery();
        q.eq(Passenger::getMemberId,LoginMemberContext.getId());
        var list = passengerMapper.selectList(q);
        return BeanUtil.copyToList(list, PassengerQueryResp.class);
    }
}
