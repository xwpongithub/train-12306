package com.jiawa.train.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiawa.train.business.entity.DailyTrainTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Mapper
public interface DailyTrainTicketMapper extends BaseMapper<DailyTrainTicket> {

    @Update("""
      <script>
      update public.daily_train_ticket
      <set>
        <if test='seatType eq "1"'>
          ydz = ydz - 1
        </if>
        <if test='seatType eq "2"'>
          edz = edz - 1
        </if>
        <if test='seatType eq "3"'>
          rw = rw - 1
        </if>
        <if test='seatType eq "4"'>
          yw = yw - 1
        </if>
      </set>
      <where>
         and date = #{date}
         and train_code = #{trainCode}
         and start_index >= #{minStartIndex}
         and start_index <![CDATA[<=]]> #{maxStartIndex}
         and end_index >= #{minEndIndex}
         and end_index <![CDATA[<=]]> #{maxEndIndex}
      </where>
      </script>
    """)
    void updateCountBySell(@Param("date") Date date, @Param("trainCode") String trainCode,
                           @Param("seatType") String seatType, @Param("minStartIndex") int minStartIndex,
                           @Param("maxStartIndex") int maxStartIndex,
                           @Param("minEndIndex") int minEndIndex,
                           @Param("maxEndIndex") int maxEndIndex);

}
