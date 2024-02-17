package com.jiawa.train.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@KeySequence(value="public.train_carriage_id_seq",dbType = DbType.POSTGRE_SQL)
@TableName(value="train_carriage",schema = "public")
public class TrainCarriage {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String trainCode;

    private Integer index;

    private String seatType;

    private Integer seatCount;

    private Integer rowCount;

    private Integer colCount;

    private Date createTime;

    private Date updateTime;

}
