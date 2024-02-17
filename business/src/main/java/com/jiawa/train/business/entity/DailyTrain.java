package com.jiawa.train.business.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value="daily_train",schema = "public")
public class DailyTrain {

    @TableId
    private Long id;

    private Date date;

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
