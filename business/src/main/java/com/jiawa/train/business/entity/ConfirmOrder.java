package com.jiawa.train.business.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmOrder {
    private Long id;

    private Long memberId;

    private Date date;

    private String trainCode;

    private String start;

    private String endVal;

    private Long dailyTrainTicketId;

    private String status;

    private Date createTime;

    private Date updateTime;

    private String tickets;

}
