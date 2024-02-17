package com.jiawa.train.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@KeySequence(value="public.train_id_seq",dbType = DbType.POSTGRE_SQL)
@TableName(value="train",schema = "public")
public class Train {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String code;

    private String type;

    private String start;

    private String startPinyin;

    private Date startTime;

    private String endVal;

    private String endPinyin;

    private Date endTime;

    private Date createTime;

    private Date updateTime;

}
