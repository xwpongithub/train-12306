package com.jiawa.train.batch.feign;

import com.jiawa.train.common.JsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

 @FeignClient(name = "train-ticket-business", url = "http://127.0.0.1:8002")
public interface BusinessFeign {

    @GetMapping("business/admin/daily-train/gen-daily/{date}")
    JsonResult genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);
}
