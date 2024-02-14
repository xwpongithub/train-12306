package com.jiawa.train.business.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DailyTrainStationQueryResp {

    /**
     * id
     */
    private Long id;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+9")
    private Date date;

    /**
     * 车次编号
     */
    private String trainCode;

    /**
     * 站序
     */
    private Integer index;

    /**
     * 站名
     */
    private String name;

    /**
     * 站名拼音
     */
    private String namePinyin;

    /**
     * 进站时间
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+9")
    private Date inTime;

    /**
     * 出站时间
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+9")
    private Date outTime;

    /**
     * 停站时长
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+9")
    private Date stopTime;

    /**
     * 里程（公里）|从上一站到本站的距离
     */
    private BigDecimal km;

    /**
     * 新增时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
