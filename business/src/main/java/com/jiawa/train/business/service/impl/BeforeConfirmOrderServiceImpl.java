package com.jiawa.train.business.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiawa.train.business.dto.ConfirmOrderMQDto;
import com.jiawa.train.business.entity.ConfirmOrder;
import com.jiawa.train.business.enums.ConfirmOrderStatusEnum;
import com.jiawa.train.business.enums.RocketMQTopicEnum;
import com.jiawa.train.business.mapper.ConfirmOrderMapper;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.service.IBeforeConfirmOrderService;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BeforeConfirmOrderServiceImpl implements IBeforeConfirmOrderService {

    private final RocketMQTemplate rocketMQTemplate;
    private final ConfirmOrderMapper confirmOrderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long beforeDoConfirmOrder(ConfirmOrderDoReq req) {
        Long id;
        var date = req.getDate();
        var trainCode = req.getTrainCode();
        var now = DateUtil.date();
        var start = req.getStart();
        var end = req.getEndVal();
        var buyingTickets = req.getTickets();
        // 2.保存确认订单，状态置为I:初始化
        var confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowflakeUtil.getSnowflakeId());
        confirmOrder.setMemberId(req.getMemberId());
        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEndVal(end);
        confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setTickets(com.alibaba.fastjson.JSON.toJSONString(buyingTickets));

        confirmOrderMapper.insert(confirmOrder);

        // 发送MQ排队购票
        ConfirmOrderMQDto confirmOrderMQDto = new ConfirmOrderMQDto();
        confirmOrderMQDto.setDate(req.getDate());
        confirmOrderMQDto.setTrainCode(req.getTrainCode());
        confirmOrderMQDto.setLogId(MDC.get("LOG_ID"));
        String reqJson = JSON.toJSONString(confirmOrderMQDto);
        rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(),reqJson);
        LogUtil.info("排队购票，发送mq结束");
        id = confirmOrder.getId();
        return id;
    }

    @Override
    /**
     * 查询前面有几个人在排队
     * @param id
     */
    public Integer queryLineCount(Long id) {
        ConfirmOrder confirmOrder = confirmOrderMapper.selectById(id);
        ConfirmOrderStatusEnum statusEnum = EnumUtil.getBy(ConfirmOrderStatusEnum::getCode, confirmOrder.getStatus());
        int result = switch (statusEnum) {
            case PENDING -> 0; // 排队0
            case SUCCESS -> -1; // 成功
            case FAILURE -> -2; // 失败
            case EMPTY -> -3; // 无票
            case CANCEL -> -4; // 取消
            case INIT -> 999; // 需要查表得到实际排队数量
        };

        if (result == 999) {
            // 排在第几位，下面的写法：where a=1 and (b=1 or c=1) 等价于 where (a=1 and b=1) or (a=1 and c=1)
            var q = Wrappers.<ConfirmOrder>lambdaQuery();
            q.or(wp->wp.eq(ConfirmOrder::getDate, confirmOrder.getDate())
                            .eq(ConfirmOrder::getTrainCode,confirmOrder.getTrainCode())
                                    .lt(ConfirmOrder::getCreateTime,confirmOrder.getCreateTime())
                    .eq(ConfirmOrder::getStatus,ConfirmOrderStatusEnum.INIT.getCode()))
                    .or(wp -> wp.eq(ConfirmOrder::getDate, confirmOrder.getDate())
                            .eq(ConfirmOrder::getTrainCode,confirmOrder.getTrainCode())
                            .lt(ConfirmOrder::getCreateTime,confirmOrder.getCreateTime())
                            .eq(ConfirmOrder::getStatus,ConfirmOrderStatusEnum.PENDING.getCode()));
            return Math.toIntExact(confirmOrderMapper.selectCount(q));
        } else {
            return result;
        }
    }

    @Override
    /**
     * 取消排队，只有I状态才能取消排队，所以按状态更新
     * @param id
     */
    public Integer cancel(Long id) {
        var q = Wrappers.<ConfirmOrder>lambdaQuery();
        q.eq(ConfirmOrder::getId,id)
                .eq(ConfirmOrder::getStatus,ConfirmOrderStatusEnum.INIT.getCode());
        var order = new ConfirmOrder();
        order.setStatus(ConfirmOrderStatusEnum.CANCEL.getCode());
        return confirmOrderMapper.update(order,q);
    }
}
