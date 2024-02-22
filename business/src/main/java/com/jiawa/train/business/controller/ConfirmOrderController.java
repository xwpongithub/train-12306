package com.jiawa.train.business.controller;

import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.service.IBeforeConfirmOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("confirm-order")
public class ConfirmOrderController {

  private final IBeforeConfirmOrderService confirmOrderService;

  @PostMapping("do")
  public Long doConfirmOrder(@Valid @RequestBody ConfirmOrderDoReq req) {
     return confirmOrderService.beforeDoConfirmOrder(req);
  }

@GetMapping("/query-line-count/{id}")
public Integer queryLineCount(@PathVariable Long id) {
    return confirmOrderService.queryLineCount(id);
}


    @GetMapping("/cancel/{id}")
    public Integer cancel(@PathVariable Long id) {
        return confirmOrderService.cancel(id);
    }

}
