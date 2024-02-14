package com.jiawa.train.business.controller;

import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.service.IConfirmOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("confirm-order")
public class ConfirmOrderController {

  private final IConfirmOrderService confirmOrderService;

  @PostMapping("do")
  public void doConfirmOrder(@Valid @RequestBody ConfirmOrderDoReq req) {
      confirmOrderService.doConfirmOrder(req);
  }

}
