package com.jiawa.train.business.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="confirm_order",schema = "public")
public class ConfirmOrder {
    @TableId
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
