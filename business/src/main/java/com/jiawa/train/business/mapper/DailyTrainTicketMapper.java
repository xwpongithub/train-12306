package com.jiawa.train.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiawa.train.business.entity.DailyTrainTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface DailyTrainTicketMapper extends BaseMapper<DailyTrainTicket> {

    void updateCountBySell(@Param("date") Date date, @Param("trainCode") String trainCode,
                           @Param("seatType") String seatType, @Param("minStartIndex") int minStartIndex,
                           @Param("maxStartIndex") int maxStartIndex,
                           @Param("minEndIndex") int minEndIndex,
                           @Param("maxEndIndex") int maxEndIndex);

}
