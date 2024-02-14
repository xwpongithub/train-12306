package com.jiawa.train.batch.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class CronJobResp {
    private String group;

    private String name;

    private String description;

    private String state;

    private String cronExpression;

    private Date nextFireTime;

    private Date preFireTime;


}
