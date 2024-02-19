package com.jiawa.train.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.service.IConfirmOrderService;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.toolkits.LogUtil;
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
  // 接口的资源名称不要和接口路径一致，会导致限流后走不到降级方法中
  @SentinelResource(value="confirmOrderDo",blockHandler = "doConfirmBlock")
  public void doConfirmOrder(@Valid @RequestBody ConfirmOrderDoReq req) {
      confirmOrderService.doConfirmOrder(req);
  }

    public void doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LogUtil.warn("请求被限流:{}",req);
        LogUtil.error(e);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }


}
