package com.jiawa.train.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@KeySequence(value="public.train_seat_id_seq",dbType = DbType.POSTGRE_SQL)
@TableName(value="train_seat",schema = "public")
public class TrainSeat {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String trainCode;

    private Integer carriageIndex;

    private String row;

    private String col;

    private String seatType;

    private Integer carriageSeatIndex;

    private Date createTime;

    private Date updateTime;


}
