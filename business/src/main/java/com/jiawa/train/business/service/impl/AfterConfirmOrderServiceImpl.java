package com.jiawa.train.business.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.Session;
import com.jiawa.train.business.entity.DailyTrainSeat;
import com.jiawa.train.business.entity.DailyTrainTicket;
import com.jiawa.train.business.feign.MemberFeign;
import com.jiawa.train.business.req.ConfirmOrderTicketReq;
import com.jiawa.train.business.service.IAfterConfirmOrderService;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.req.MemberTicketReq;
import com.jiawa.train.common.toolkits.LogUtil;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AfterConfirmOrderServiceImpl implements IAfterConfirmOrderService {


    private final MemberFeign memberFeign;

    /**
     * 选中座位后事务处理：
     *  座位表修改售卖情况sell；
     *  余票详情表修改余票；
     *  为会员增加购票记录
     *  更新确认订单为成功
     */
    @GlobalTransactional
    @Override
    public void afterDoConfirm(
            DailyTrainTicket dailyTrainTicket,
            List<DailyTrainSeat> finalSeatList,
            List<ConfirmOrderTicketReq> tickets,
            Session session) throws SQLException {
        LogUtil.info("【Seata全局事务ID - afterDoConfirm:{}】", RootContext.getXID());
        var now = DateUtil.date();
        for (var i = 0;i<finalSeatList.size();i++) {
            DailyTrainSeat seat = finalSeatList.get(i);
            //  1.座位表修改售卖情况sell
            var updateConditions = Entity.create("public.daily_train_seat");
            updateConditions.set("id", seat.getId());
            Entity updateExample = Entity.create("public.daily_train_seat");
            updateExample.set("sell", seat.getSell())
                    .set("create_time", now)
                    .set("update_time", now);
            session.update(updateExample, updateConditions);

            /// 2.余票详情表修改余票；
            // 计算这个站卖出去后，影响了哪些站的余票库存
            // 参照2-3节 如何保证不超卖、不少卖，还要能承受极高的并发 10:30左右
            // 影响的库存：本次选座之前没卖过票的，和本次购买的区间有交集的区间
            // 假设10个站，本次买4~7站
            // 原售：001000001
            // 购买：000011100
            // 新售：001011101
            // 影响：XXX11111X
            // Integer startIndex = 4;
            // Integer endIndex = 7;
            // Integer minStartIndex = startIndex - 往前碰到的最后一个0;
            // Integer maxStartIndex = endIndex - 1;
            // Integer minEndIndex = startIndex + 1;
            // Integer maxEndIndex = endIndex + 往后碰到的最后一个0;
            var startIndex = dailyTrainTicket.getStartIndex();
            var endIndex = dailyTrainTicket.getEndIndex();
            var sell = seat.getSell();
            var chars = sell.toCharArray();
            var maxStartIndex = endIndex - 1;
            var minEndIndex = startIndex + 1;
            var minStartIndex = 0;
            for (var j = startIndex - 1; j >= 0; j--) {
                var aChar = chars[j];
                if (aChar == '1') {
                    minStartIndex = j + 1;
                    break;
                }
            }
            LogUtil.debug("影响出发站区间：{}-{}", minStartIndex, maxStartIndex);

            var maxEndIndex = sell.length();
            for (var j = endIndex; j < maxEndIndex; j++) {
                var aChar = chars[j];
                if (aChar == '1') {
                    maxEndIndex = j;
                    break;
                }
            }
            LogUtil.debug("影响到达站区间：{}-{}", minEndIndex, maxEndIndex);

            updateCountSell(session, seat, minStartIndex, maxStartIndex, minEndIndex, maxEndIndex);

            // 3.为会员增加购票记录(购票成功后调用member的接口)
            var memberTicketReq = new MemberTicketReq();
            memberTicketReq.setMemberId(LoginMemberContext.getId());
            memberTicketReq.setPassengerId(tickets.get(i).getPassengerId());
            memberTicketReq.setPassengerName(tickets.get(i).getPassengerName());
            memberTicketReq.setTrainDate(dailyTrainTicket.getDate());
            memberTicketReq.setTrainCode(dailyTrainTicket.getTrainCode());
            memberTicketReq.setCarriageIndex(seat.getCarriageIndex());
            memberTicketReq.setSeatRow(seat.getRow());
            memberTicketReq.setSeatCol(seat.getCol());
            memberTicketReq.setStartStation(dailyTrainTicket.getStart());
            memberTicketReq.setStartTime(dailyTrainTicket.getStartTime());
            memberTicketReq.setEndStation(dailyTrainTicket.getEndVal());
            memberTicketReq.setEndTime(dailyTrainTicket.getEndTime());
            memberTicketReq.setSeatType(seat.getSeatType());
            var saveJsonResult = memberFeign.save(memberTicketReq);
            LogUtil.debug("调用member接口，返回：{}",  saveJsonResult);
        }
    }

    private void updateCountSell(Session session, DailyTrainSeat seat, int minStartIndex, int maxStartIndex, int minEndIndex, int maxEndIndex) throws SQLException {
        var seatType = seat.getSeatType();
        var sql = "update public.daily_train_ticket ";
        if (StrUtil.equals(seatType, "1")) {
            sql += "set ydz = ydz - 1";
        }
        if (StrUtil.equals(seatType, "2")) {
            sql += "set edz = edz - 1";
        }
        if (StrUtil.equals(seatType, "3")) {
            sql += "set rw = rw - 1";
        }
        if (StrUtil.equals(seatType, "4")) {
            sql += "set yw = yw - 1 ";
        }
        sql += """
               where
                     date = ?
                     and train_code = ?
                     and start_index >= ?
                     and start_index <= ?
                     and end_index >= ?
                     and end_index <= ?
            """;
        session.execute(sql, seat.getDate(), seat.getTrainCode(), minStartIndex, maxStartIndex, minEndIndex, maxEndIndex);
    }

}
