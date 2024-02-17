package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiawa.train.business.entity.DailyTrainSeat;
import com.jiawa.train.business.mapper.DailyTrainSeatMapper;
import com.jiawa.train.business.req.DailyTrainSeatQueryReq;
import com.jiawa.train.business.req.DailyTrainSeatSaveReq;
import com.jiawa.train.business.req.SeatSellReq;
import com.jiawa.train.business.resp.DailyTrainSeatQueryResp;
import com.jiawa.train.business.resp.SeatSellResp;
import com.jiawa.train.business.service.IDailyTrainSeatService;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.toolkits.LogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DailyTrainSeatServiceImpl implements IDailyTrainSeatService {

    private final DailyTrainSeatMapper dailyTrainSeatMapper;

    @Override
    public void save(DailyTrainSeatSaveReq req) {
        var now = DateUtil.date();
        var dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);
        if (Objects.isNull(dailyTrainSeat.getId())) {
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        } else {
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.updateById(dailyTrainSeat);
        }
    }

    @Override
    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req) {
        var q = Wrappers.<DailyTrainSeat>lambdaQuery();
        q.orderByDesc(DailyTrainSeat::getDate)
                .orderByAsc(DailyTrainSeat::getTrainCode,
                        DailyTrainSeat::getCarriageIndex,
                        DailyTrainSeat::getCarriageSeatIndex)
                .eq(StrUtil.isNotBlank(req.getTrainCode()),DailyTrainSeat::getTrainCode,
                        req.getTrainCode());
        var p = new Page<DailyTrainSeat>(req.getPage(),req.getSize());
        var dbPage =dailyTrainSeatMapper.selectPage(p,q);
        var resp = new PageResp<DailyTrainSeatQueryResp>();
        var list = BeanUtil.copyToList(dbPage.getRecords() , DailyTrainSeatQueryResp.class);
        resp.setTotal((int)dbPage.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainSeatMapper.deleteById(id);
    }

    @Override
    public int countSeat(Date date, String trainCode) {
        return countSeat(date, trainCode, null);
    }

    @Override
    public int countSeat(Date date, String trainCode, String seatType) {
        var q = Wrappers.<DailyTrainSeat>lambdaQuery();
        q.eq(DailyTrainSeat::getDate,date)
                .eq(DailyTrainSeat::getTrainCode,trainCode)
                .eq(StrUtil.isNotBlank(seatType),DailyTrainSeat::getSeatType,seatType);
        long l = dailyTrainSeatMapper.selectCount(q);
        // 如果根本没有seatType对应的座位，则返回-1给前端，让前端对显示进行区分
        // -1表示没有此类型的座位,前端显示"-"
        // 0表示没有余票可卖，前端显示"无"
        // 余票数量大于20，前端显示"有"
        // 如果小于等于20前端显示余票数量
        if (l == 0L) {
            return -1;
        }
        return (int) l;
    }

    @Override
    public List<DailyTrainSeat> selectByCarriage(Date date, String trainCode, Integer carriageIndex) {
        var q = Wrappers.<DailyTrainSeat>lambdaQuery();
        q.orderByAsc(DailyTrainSeat::getCarriageSeatIndex)
                .eq(DailyTrainSeat::getDate,date)
                .eq(DailyTrainSeat::getTrainCode,trainCode)
                .eq(DailyTrainSeat::getCarriageIndex,carriageIndex);
        return dailyTrainSeatMapper.selectList(q);
    }

    /**
     * 查询某日某车次的所有座位
     */
    @Override
    public List<SeatSellResp> querySeatSell(SeatSellReq req) {
        var date = req.getDate();
        var trainCode = req.getTrainCode();
        LogUtil.debug("查询日期【{}】车次【{}】的座位销售信息", DateUtil.formatDate(date), trainCode);
        var q = Wrappers.<DailyTrainSeat>lambdaQuery();
        q.orderByAsc(DailyTrainSeat::getCarriageIndex,DailyTrainSeat::getCarriageSeatIndex)
                .eq(DailyTrainSeat::getDate,date)
                .eq(DailyTrainSeat::getTrainCode,trainCode);
        var list = dailyTrainSeatMapper.selectList(q);
        return BeanUtil.copyToList(list, SeatSellResp.class);
    }


}
