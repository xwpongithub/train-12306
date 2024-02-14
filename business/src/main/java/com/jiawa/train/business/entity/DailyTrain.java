package com.jiawa.train.business.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyTrain {

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
