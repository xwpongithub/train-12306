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
@TableName(value="station",schema = "public")
public class Station {

  @TableId
  private Long id;

  private String name;

  private String namePinyin;

  private String namePy;

  private Date createTime;

  private Date updateTime;

}
