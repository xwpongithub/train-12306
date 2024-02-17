package com.jiawa.train.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@KeySequence(value="public.train_station_id_seq",dbType = DbType.POSTGRE_SQL)
@TableName(value="train_station",schema = "public")
public class TrainStation {
    @TableId(type = IdType.INPUT)
    private Long id;

    private String trainCode;

    private Integer index;

    private String name;

    private String namePinyin;

    private Date inTime;

    private Date outTime;

    private Date stopTime;

    private BigDecimal km;

    private Date createTime;

    private Date updateTime;

}
