package com.jiawa.train.business.mq;

import com.alibaba.fastjson.JSON;
import com.jiawa.train.business.dto.ConfirmOrderMQDto;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.service.IConfirmOrderService;
import com.jiawa.train.common.toolkits.LogUtil;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

 @Service
 @RequiredArgsConstructor
 @RocketMQMessageListener(consumerGroup = "default", topic = "CONFIRM_ORDER")
 public class ConfirmOrderConsumer implements RocketMQListener<MessageExt> {

     private final IConfirmOrderService confirmOrderService;

     @Override
     public void onMessage(MessageExt messageExt) {

         byte[] body = messageExt.getBody();
         ConfirmOrderMQDto req = JSON.parseObject(new String(body), ConfirmOrderMQDto.class);

         MDC.put("LOG_ID", req.getLogId());
         LogUtil.info("ROCKETMQ收到消息：{}", new String(body));
         confirmOrderService.doConfirmOrder(req);
     }
 }
