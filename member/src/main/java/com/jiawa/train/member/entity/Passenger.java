package com.jiawa.train.member.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    private Long id;
    private Long memberId;
    private String name;
    private String idCard;
    private String type;
    private Date createTime;
    private Date updateTime;

}
