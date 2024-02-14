package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.jiawa.train.business.entity.DailyTrainSeat;
import com.jiawa.train.business.mapper.DailyTrainSeatMapper;
import com.jiawa.train.business.req.DailyTrainSeatQueryReq;
import com.jiawa.train.business.req.DailyTrainSeatSaveReq;
import com.jiawa.train.business.req.SeatSellReq;
import com.jiawa.train.business.resp.DailyTrainSeatQueryResp;
import com.jiawa.train.business.resp.SeatSellResp;
import com.jiawa.train.business.service.IDailyTrainSeatService;
import com.jiawa.train.common.resp.PageResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
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
            dailyTrainSeatMapper.insertSelective(dailyTrainSeat);
        } else {
            dailyTrainSeat.setUpdateTime(now);
            var q = new HashMap<String,Object>();
            q.put("id",req.getId());
            dailyTrainSeatMapper.updateSelective(dailyTrainSeat,q);
        }
    }

    @Override
    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req) {
        var trainList =dailyTrainSeatMapper.page(req.getPage(), req.getSize(),req.getTrainCode());
        var resp = new PageResp<DailyTrainSeatQueryResp>();
        var list = BeanUtil.copyToList(trainList , DailyTrainSeatQueryResp.class);
        resp.setTotal(trainList.getTotal());
        resp.setList(list);
        return resp;
    }

    @Override
    public void delete(Long id) {
        dailyTrainSeatMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int countSeat(Date date, String trainCode) {
        return countSeat(date, trainCode, null);
    }

    @Override
    public int countSeat(Date date, String trainCode, String seatType) {
        var q = new HashMap<String,Object>();
        q.put("date",date);
        q.put("train_code",trainCode);
        if (StrUtil.isNotBlank(seatType)){
            q.put("seat_type",seatType);
        }
        long l = dailyTrainSeatMapper.countExample(q);
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
        var q = new HashMap<String,Object>();
        q.put("date",date);
        q.put("train_code",trainCode);
        q.put("carriage_index",carriageIndex);
        return dailyTrainSeatMapper.selectByExample(q);
    }

    /**
     * 查询某日某车次的所有座位
     */
    @Override
    public List<SeatSellResp> querySeatSell(SeatSellReq req) {
        var q = new HashMap<String,Object>();
        var date = req.getDate();
        var trainCode = req.getTrainCode();
        q.put("date",date);
        q.put("train_code",trainCode);
        var list = dailyTrainSeatMapper.selectByExample(q);
        return BeanUtil.copyToList(list, SeatSellResp.class);
    }


}
