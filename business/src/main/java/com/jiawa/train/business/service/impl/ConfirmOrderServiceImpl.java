package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiawa.train.business.entity.ConfirmOrder;
import com.jiawa.train.business.entity.DailyTrainSeat;
import com.jiawa.train.business.entity.DailyTrainTicket;
import com.jiawa.train.business.enums.ConfirmOrderStatusEnum;
import com.jiawa.train.business.enums.SeatColEnum;
import com.jiawa.train.business.enums.SeatTypeEnum;
import com.jiawa.train.business.feign.MemberFeign;
import com.jiawa.train.business.mapper.ConfirmOrderMapper;
import com.jiawa.train.business.mapper.DailyTrainSeatMapper;
import com.jiawa.train.business.mapper.DailyTrainTicketMapper;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.req.ConfirmOrderQueryReq;
import com.jiawa.train.business.req.ConfirmOrderTicketReq;
import com.jiawa.train.business.resp.ConfirmOrderQueryResp;
import com.jiawa.train.business.service.IConfirmOrderService;
import com.jiawa.train.business.service.IDailyTrainCarriageService;
import com.jiawa.train.business.service.IDailyTrainSeatService;
import com.jiawa.train.business.service.IDailyTrainTicketService;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.req.MemberTicketReq;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ConfirmOrderServiceImpl implements IConfirmOrderService {

    private final ConfirmOrderMapper confirmOrderMapper;

    @Override
    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        var q = Wrappers.<ConfirmOrder>lambdaQuery();
        q.orderByDesc(ConfirmOrder::getId);
        var p = new Page<ConfirmOrder>(req.getPage(),req.getSize());
        var dbPage =confirmOrderMapper.selectPage(p,q);
        var resp = new PageResp<ConfirmOrderQueryResp>();
        var list = BeanUtil.copyToList(dbPage.getRecords() , ConfirmOrderQueryResp.class);
        resp.setTotal((int)dbPage.getTotal());
        resp.setList(list);
        return resp;
    }

    private final IDailyTrainTicketService dailyTrainTicketService;
    private final IDailyTrainCarriageService dailyTrainCarriageService;
    private final IDailyTrainSeatService dailyTrainSeatService;
    private final RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
//    @SentinelResource(value="doConfirmOrder",blockHandler = "doConfirmBlock")
    public void doConfirmOrder(ConfirmOrderDoReq req) {
        var date = req.getDate();
        var trainCode = req.getTrainCode();
        var now = DateUtil.date();
        var lockKey = DateUtil.formatDate(date)+":"+trainCode;
        RLock lock = null;
        var start = req.getStart();
        var end = req.getEndVal();
        var buyingTickets = req.getTickets();
        LogUtil.debug("确认订单请求参数:{}",req);
        try {
            lock = redissonClient.getLock(lockKey);
            // 不带看门狗模式
            // var tryLock = lock.tryLock(30, 0, TimeUnit.SECONDS);
            // 带看门狗模式
            var tryLock = lock.tryLock(0, TimeUnit.SECONDS);
            if (tryLock) {
                LogUtil.info("恭喜，抢到锁了！");
            } else {
                LogUtil.info("很遗憾，没抢到锁");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
            }

            // 1.查出余票记录，需要得到真实的库存 数据校验（如：车次是否存在，余票是否存在，车次是否在有效期内，tickets的条数大于0，同乘客同车次是否已买过票）
            var stockTickets = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
            LogUtil.debug("日期{}下查出余票记录为{}", date, stockTickets);

            // 2.保存确认订单，状态置为I:初始化
            var confirmOrder = new ConfirmOrder();
            confirmOrder.setId(SnowflakeUtil.getSnowflakeId());
            confirmOrder.setMemberId(LoginMemberContext.getId());
            confirmOrder.setDate(date);
            confirmOrder.setTrainCode(trainCode);
            confirmOrder.setStart(start);
            confirmOrder.setEndVal(end);
            confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrder.setTickets(JSON.toJSONString(buyingTickets));

            confirmOrderMapper.insert(confirmOrder);

            // 4.预扣减余票数量并且判断余票是否充足
            reduceTickets(buyingTickets, stockTickets);

            // 选座时计算其他座位相对第一个座位的偏移值
            // 比如选择的是C1,D2,则偏移值是:[0,5]
            // 比如选择的是A1,B1,C1,则偏移值是:[0,1,2]
            var firstTicket = buyingTickets.get(0);
            // 最终的选座结果
            var finalSeatList = CollUtil.<DailyTrainSeat>newArrayList();
            // 5.选座
            // 5.1.1 一个车厢一个车厢的获取座位数据
            // 5.1.2 挑选符合条件的座位，如果这个车厢不符合选座条件，则进入下一个车厢继续挑选（多个选座应该在同一个车厢）
            if (StrUtil.isBlank(firstTicket.getSeat())) {
                LogUtil.debug("本次购票没有进行选座");
                for (var buyingTicket : buyingTickets) {
                    chooseSeat(
                            finalSeatList,
                            date,
                            trainCode,
                            buyingTicket.getSeatTypeCode(),
                            null,
                            null,
                            stockTickets.getStartIndex(),
                            stockTickets.getEndIndex());
                }
            } else {
                // 计算偏移值
                var colEnumList = SeatColEnum.getColsByType(firstTicket.getSeatTypeCode());
                LogUtil.debug("本次选座的座位类型包含的列:{}", colEnumList);
                // 组成和前端两排选座一样的列表，用于做参照的作为列表，如：referSeatList = {A1,C1,D1,F1}
                var referSeatList = CollUtil.<String>newArrayList();
                for (int i = 1; i <= 2; i++) {
                    for (var seatColEnum : colEnumList) {
                        referSeatList.add(seatColEnum.getCode() + i);
                    }
                }
                LogUtil.debug("用于做参照的两排座位:{}", referSeatList);
                // 绝对偏移值，即：在参照作为列表中的位置
                var absoluteOffsetList = CollUtil.<Integer>newArrayList();
                for (var ticket : buyingTickets) {
                    var seat = ticket.getSeat();
                    var index = referSeatList.indexOf(seat);
                    absoluteOffsetList.add(index);
                }
                LogUtil.debug("计算得到所有座位的绝对偏移值：{}", absoluteOffsetList);
                var offsetList = CollUtil.<Integer>newArrayList();
                for (var index : absoluteOffsetList) {
                    var offset = index - absoluteOffsetList.get(0);
                    offsetList.add(offset);
                }
                LogUtil.debug("计算得到座位的相对第一个座位的偏移值：{}", offsetList);
                chooseSeat(
                        finalSeatList,
                        date,
                        trainCode,
                        firstTicket.getSeatTypeCode(),
                        firstTicket.getSeat().split("")[0],
                        offsetList,
                        stockTickets.getStartIndex(),
                        stockTickets.getEndIndex());
            }
            LogUtil.debug("最终选座:{}", finalSeatList);
            // 6.选座后进行事务处理
            // 6.1.1 座位表修改售卖情况sell字段
            // 6.1.2 修改余票详情表的余票数量
            // 6.1.3 为会员增加购票记录
            // 6.1.4 更新订单状态
            try {
                afterDoConfirm(stockTickets, finalSeatList, buyingTickets, confirmOrder);
            } catch (Exception e) {
                LogUtil.warn("保存购票信息失败");
                LogUtil.error(e);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
            }
        } catch (InterruptedException e){
            LogUtil.warn("购票异常");
            LogUtil.error(e);
        } finally {
            LogUtil.info("购票流程结束，锁释放！");
            // lock不为空且是当前的线程才能去删除锁
            if (Objects.nonNull(lock) && lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

//    public void doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
//        LogUtil.warn("请求被限流:{}",req);
//        LogUtil.error(e);
//        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
//    }


    private final MemberFeign memberFeign;
    private final DailyTrainTicketMapper dailyTrainTicketMapper;
    private final DailyTrainSeatMapper dailyTrainSeatMapper;

    /**
     * 选中座位后事务处理：
     *  座位表修改售卖情况sell；
     *  余票详情表修改余票；
     *  为会员增加购票记录
     *  更新确认订单为成功
     */
    private void afterDoConfirm(
                               DailyTrainTicket dailyTrainTicket,
                               List<DailyTrainSeat> finalSeatList,
                               List<ConfirmOrderTicketReq> tickets,
                               ConfirmOrder confirmOrder) {
        var now = DateUtil.date();
        for (var i = 0;i<finalSeatList.size();i++) {
            DailyTrainSeat seat = finalSeatList.get(i);
            //  1.座位表修改售卖情况sell

            var seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(seat.getId());
            seatForUpdate.setSell(seat.getSell());
            seatForUpdate.setUpdateTime(now);
            dailyTrainSeatMapper.updateById(seatForUpdate);

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

            dailyTrainTicketMapper.updateCountBySell(
                    seat.getDate(),
                    seat.getTrainCode(),
                    seat.getSeatType(),
                    minStartIndex, maxStartIndex, minEndIndex, maxEndIndex);

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

            // 4.更新确认订单表状态为S:成功
            //// 如果对接支付，这里变更状态应该先变为处理中，等待支付接口返回成功后订单状态才能变为SUCCESS，这里因为不做支付接口就直接将订单状态变为SUCCESS
            var confirmOrderForUpdate = new ConfirmOrder();
            confirmOrderForUpdate.setId(confirmOrder.getId());
            confirmOrderForUpdate.setUpdateTime(now);
            confirmOrderForUpdate.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
            confirmOrderMapper.updateById(confirmOrderForUpdate);

        }
    }


    private void reduceTickets( List<ConfirmOrderTicketReq> buyingTickets, DailyTrainTicket stockTickets) {
        for (ConfirmOrderTicketReq buyingTicket : buyingTickets) {
            var seatTypeCode = buyingTicket.getSeatTypeCode();
            var seatTypeEnum = SeatTypeEnum.getEnumByCode(seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = stockTickets.getYdz() -1;
                    if (countLeft < 0) {
                        // 一等座的余票不足
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    stockTickets.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = stockTickets.getEdz() -1;
                    if (countLeft < 0) {
                        // 一等座的余票不足
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    stockTickets.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = stockTickets.getRw() -1;
                    if (countLeft < 0) {
                        // 一等座的余票不足
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    stockTickets.setRw(countLeft);
                }
                case YW ->  {
                    int countLeft = stockTickets.getYw() -1;
                    if (countLeft < 0) {
                        // 一等座的余票不足
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    stockTickets.setYw(countLeft);
                }
            }
        }
    }

    /**
     * 如果有座位就一次性挑完，如果没有座位则一个一个选
     * @param date
     * @param trainCode
     * @param seatType
     * @param selectedCol 要选的是哪一列
     * @param offsetList
     * @param startIndex
     * @param endIndex
     */
    private void chooseSeat(
            List<DailyTrainSeat> finalSeatList,
            Date date,
            String trainCode,
            String seatType,
            String selectedCol,
            List<Integer> offsetList,
            Integer startIndex,
            Integer endIndex) {
        var getSeatList = CollUtil.<DailyTrainSeat>newArrayList();
        var carriageList =dailyTrainCarriageService.selectBySeatType(date,trainCode,seatType);
        LogUtil.debug("共查出{}个符合条件的车厢",carriageList);
        for (var dailyTrainCarriageEntity : carriageList) {
            getSeatList.clear();
            var carriageIndex = dailyTrainCarriageEntity.getIndex();
            LogUtil.debug("开始从车厢{}选座",carriageIndex);
            var seatList = dailyTrainSeatService.selectByCarriage(date,trainCode,carriageIndex);
            LogUtil.debug("车厢{}的座位数：{}",carriageIndex,seatList.size());
            for (var i =0;i< seatList.size(); i++) {
                var dailyTrainSeat = seatList.get(i);
                boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                var seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                // 判断column，有值的话要对比列号
                var col = dailyTrainSeat.getCol();
                var alreadyChooseFlag = false;
                for (var trainSeatEntity : finalSeatList) {
                    if (trainSeatEntity.getId().equals(dailyTrainSeat.getId())){
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    LogUtil.debug("座位{}已被选中，不能重复选中，继续判断下一个座位",seatIndex);
                    continue;
                }
                if (StrUtil.isBlank(selectedCol)) {
                    LogUtil.debug("无选座");
                } else {
                    if (!StrUtil.equals(col,selectedCol)){
                        LogUtil.debug("座位{}列值不对，继续判断下一个座位,当前列值:{},目标列值:{}",seatIndex,col,selectedCol);
                        continue;
                    }
                }

                if (!isChoose){
                    continue;
                } else {
                    getSeatList.add(dailyTrainSeat);
                    LogUtil.debug("选中座位");
                }
                // 根据offset选剩下的座位
                var isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)){
                    LogUtil.debug("有偏移值:{},校验偏移的座位是否可选",offsetList);
                    for (int j = 1; j < offsetList.size(); j++) {
                        // 从索引1开始，索引0就是当前已选中的票
                        var offset = offsetList.get(j);
                        // 座位在库里的索引是从1开始
                        var nextIndex = i + offset;
                        // 有选座时一定是在同一个车厢
                        if (nextIndex >= seatList.size()){
                            LogUtil.debug("座位{}不可选，偏移后的索引超出了这个车厢的座位数",nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }

                        var nextDailyTrainSeat = seatList.get(nextIndex);
                        var isChooseNext = calSell(nextDailyTrainSeat,startIndex,endIndex);
                        if (isChooseNext){
                            LogUtil.debug("选中座位{}",nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            LogUtil.debug("座位{}不可选",nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }

                if (!isGetAllOffsetSeat){
                    getSeatList.clear();
                    continue;
                }

                // 保存选好的座位到数据库
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    /**
     * 计算某座位在某区间是否可售卖
     * 例：sell = 10001,本次购买区间站1~4，则区间已售000
     * 全部为0表示这个区间可买，只要有1，表示区间内已售过票
     * 选中后，要计算售票后的sell，比如原来是10001，本次购买区间站1~4
     * 方案：构造本次购票造成的售卖信息01110，和原sell 10001按位或，最终得到11111
     * @return true 选中 false 未选中
     */
    private boolean calSell(DailyTrainSeat dailyTrainSeatEntity,int startIndex,int endIndex) {
        // 10001
        var sell = dailyTrainSeatEntity.getSell();
        // 000
        var sellPart = sell.substring(startIndex,endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            LogUtil.debug("座位{}造本次车站区间{}-{}已被售出，不可被选中",dailyTrainSeatEntity.getCarriageSeatIndex(),
                    startIndex,endIndex);
            return false;
        } else {
            LogUtil.debug("座位{}造本次车站区间{}-{}未被售出，可被选中",dailyTrainSeatEntity.getCarriageSeatIndex(),
                    startIndex,endIndex);
            // 111
            var curSell = sellPart.replace('0','1');
            // 0111
            curSell = StrUtil.fillBefore(curSell,'0', endIndex);
            // 01110
            curSell = StrUtil.fillAfter(curSell,'0',sell.length());
            // 当前区间的售票信息curSell与库里的已售信息进行按位或运算，即可得到该座位卖出此票后的售票情况
            var newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            var newSell = NumberUtil.getBinaryStr(newSellInt);
            newSell = StrUtil.fillBefore(newSell,'0',sell.length());
            LogUtil.debug("座位{}被选中,原售票信息:{},车站区间:{}-{},即:{},最终售票信息:{}",
                    dailyTrainSeatEntity.getCarriageSeatIndex(),sell,startIndex,
                    endIndex,curSell,newSell);
            dailyTrainSeatEntity.setSell(newSell);
            return true;
        }
    }


}
