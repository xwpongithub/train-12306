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
@TableName(value="sk_token",schema = "public")
public class SkToken {
    @TableId
    private Long id;

    private Date date;

    private String trainCode;

    private Integer count;

    private Date createTime;

    private Date updateTime;

}
