package com.jiawa.train.business.resp;

import lombok.Data;

import java.util.Date;

@Data
public class StationQueryResp {


    /**
     * id
     */
    private Long id;

    /**
     * 站名
     */
    private String name;

    /**
     * 站名拼音
     */
    private String namePinyin;

    /**
     * 站名拼音首字母
     */
    private String namePy;

    /**
     * 新增时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}
