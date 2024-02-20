package com.jiawa.train.business.controller;

import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.service.IConfirmOrderService;
import com.jiawa.train.business.util.RedisUtil;
import com.jiawa.train.common.JsonResult;
import com.jiawa.train.common.toolkits.LogUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("confirm-order")
public class ConfirmOrderController {

  private final IConfirmOrderService confirmOrderService;
  private final RedisUtil redisUtil;
    @Value("${spring.profiles.active}")
    private String env;
  @PostMapping("do")
  public JsonResult doConfirmOrder(@Valid @RequestBody ConfirmOrderDoReq req) {

      if (!env.equals("dev")) {
          // 图形验证码校验
          String imageCodeToken = req.getImageCodeToken();
          String imageCode = req.getImageCode();
          String imageCodeRedis = redisUtil.get(imageCodeToken);
          LogUtil.info("从redis中获取到的验证码：{}", imageCodeRedis);
          if (Objects.isNull(imageCodeRedis)) {
              return JsonResult.error("验证码已过期");
          }
          // 验证码校验，大小写忽略，提升体验，比如Oo Vv Ww容易混
          if (!imageCodeRedis.equalsIgnoreCase(imageCode)) {
              return JsonResult.error("验证码不正确");
          } else {
              // 验证通过后，移除验证码
              redisUtil.del(imageCodeToken);
          }
      }
      return JsonResult.ok("购票成功");
  }

}
