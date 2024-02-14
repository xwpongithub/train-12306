package com.jiawa.train.business.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Station {

  private Long id;

  private String name;

  private String namePinyin;

  private String namePy;

  private Date createTime;

  private Date updateTime;

}
