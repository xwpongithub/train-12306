package com.jiawa.train.business.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrainSeatSaveReq {

    /**
     * id
     */
    private Long id;

    /**
     * 车次编号
     */
    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    /**
     * 厢序
     */
    @NotNull(message = "【厢序】不能为空")
    private Integer carriageIndex;

    /**
     * 排号|01, 02
     */
    @NotBlank(message = "【排号】不能为空")
    private String row;

    /**
     * 列号|枚举[SeatColEnum]
     */
    @NotBlank(message = "【列号】不能为空")
    private String col;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    @NotBlank(message = "【座位类型】不能为空")
    private String seatType;

    /**
     * 同车厢座序
     */
    @NotNull(message = "【同车厢座序】不能为空")
    private Integer carriageSeatIndex;


}
