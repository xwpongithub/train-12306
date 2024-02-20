package com.jiawa.train.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiawa.train.business.entity.SkToken;
import com.jiawa.train.business.enums.RedisKeyPreEnum;
import com.jiawa.train.business.mapper.SkTokenMapper;
import com.jiawa.train.business.req.SkTokenQueryReq;
import com.jiawa.train.business.req.SkTokenSaveReq;
import com.jiawa.train.business.resp.SkTokenQueryResp;
import com.jiawa.train.business.service.IDailyTrainSeatService;
import com.jiawa.train.business.service.IDailyTrainStationService;
import com.jiawa.train.business.service.ISkTokenService;
import com.jiawa.train.business.util.RedisUtil;
import com.jiawa.train.common.resp.PageResp;
import com.jiawa.train.common.toolkits.LogUtil;
import com.jiawa.train.common.toolkits.SnowflakeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ISkTokenServiceImpl implements ISkTokenService {

    private final IDailyTrainSeatService dailyTrainSeatService;
    private final IDailyTrainStationService dailyTrainStationService;
    private final SkTokenMapper skTokenMapper;
    private final RedisUtil redisUtil;

    /**
     * 初始化
     */
    @Override
    public void genDaily(Date date, String trainCode) {
        LogUtil.info("删除日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
        var delQ = Wrappers.<SkToken>lambdaQuery();
        delQ.eq(SkToken::getDate, date)
                .eq(SkToken::getTrainCode, trainCode);
        skTokenMapper.delete(delQ);
        var now = DateUtil.date();
        SkToken skToken = new SkToken();
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setId(SnowflakeUtil.getSnowflakeId());
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
        LogUtil.info("车次【{}】座位数：{}", trainCode, seatCount);

        long stationCount = dailyTrainStationService.countByTrainCode(date, trainCode);
        LogUtil.info("车次【{}】到站数：{}", trainCode, stationCount);

        // 3/4需要根据实际卖票比例来定，一趟火车最多可以卖（seatCount * stationCount）张火车票
        int count = (int) (seatCount * stationCount); // * 3/4);
        LogUtil.info("车次【{}】初始生成令牌数：{}", trainCode, count);
        skToken.setCount(count);

        skTokenMapper.insert(skToken);
    }

    @Override
    public void save(SkTokenSaveReq req) {
        var now = DateUtil.date();
        var skToken = BeanUtil.copyProperties(req, SkToken.class);
        if (Objects.isNull(skToken.getId())) {
            skToken.setId(SnowflakeUtil.getSnowflakeId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateById(skToken);
        }
    }

    @Override
    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req) {
        var p = new Page<SkToken>(req.getPage(),req.getSize());
        var q = Wrappers.<SkToken>lambdaQuery();
                        q.orderByDesc(SkToken::getId);
        var dbPage = skTokenMapper.selectPage(p,q);

        var list = BeanUtil.copyToList(dbPage.getRecords(), SkTokenQueryResp.class);

        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal((int)dbPage.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    @Override
    public void delete(Long id) {
        skTokenMapper.deleteById(id);
    }

    /**
     * 校验令牌
     */
    public boolean validSkToken(Date date, String trainCode, Long memberId) {
        LogUtil.info("会员【{}】获取日期【{}】车次【{}】的令牌开始", memberId, DateUtil.formatDate(date), trainCode);

        // 需要去掉这段，否则发布生产后，体验多人排队功能时，会因拿不到锁而返回：等待5秒，加入20人时，只有第1次循环能拿到锁
        // if (!env.equals("dev")) {
        //     // 先获取令牌锁，再校验令牌余量，防止机器人抢票，lockKey就是令牌，用来表示【谁能做什么】的一个凭证
        //     String lockKey = RedisKeyPreEnum.SK_TOKEN + "-" + DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
        //     Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        //     if (Boolean.TRUE.equals(setIfAbsent)) {
        //         LOG.info("恭喜，抢到令牌锁了！lockKey：{}", lockKey);
        //     } else {
        //         LOG.info("很遗憾，没抢到令牌锁！lockKey：{}", lockKey);
        //         return false;
        //     }
        // }

        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT + "-" + DateUtil.formatDate(date) + "-" + trainCode;
        Object skTokenCount = redisUtil.get(skTokenCountKey);
        if (skTokenCount != null) {
            LogUtil.info("缓存中有该车次令牌大闸的key：{}", skTokenCountKey);
            var count = redisUtil.decr(skTokenCountKey, 1);
            if (count < 0L) {
                LogUtil.warn("获取令牌失败：{}", skTokenCountKey);
                return false;
            } else {
                LogUtil.info("获取令牌后，令牌余数：{}", count);
                redisUtil.expire(skTokenCountKey, 60);
                // 每获取5个令牌更新一次数据库
                if (count % 5 == 0) {
                    skTokenMapper.decrease(date, trainCode, 5);
                }
                return true;
            }
        } else {
            LogUtil.info("缓存中没有该车次令牌大闸的key：{}", skTokenCountKey);
            // 检查是否还有令牌
            var q = Wrappers.<SkToken>lambdaQuery();
            q.eq(SkToken::getDate,date)
                    .eq(SkToken::getTrainCode,trainCode);
            var tokenCountList = skTokenMapper.selectList(q);
            if (CollUtil.isEmpty(tokenCountList)) {
                LogUtil.info("找不到日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
                return false;
            }

            SkToken skToken = tokenCountList.get(0);
            if (skToken.getCount() <= 0) {
                LogUtil.info("日期【{}】车次【{}】的令牌余量为0", DateUtil.formatDate(date), trainCode);
                return false;
            }

            // 令牌还有余量
            // 令牌余数-1
            Integer count = skToken.getCount() - 1;
            skToken.setCount(count);
            LogUtil.info("将该车次令牌大闸放入缓存中，key: {}， count: {}", skTokenCountKey, count);
            // 不需要更新数据库，只要放缓存即可
            redisUtil.set(skTokenCountKey, String.valueOf(count), 60);
            // skTokenMapper.updateByPrimaryKey(skToken);
            return true;
        }

        // 令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程去判断库存，判断令牌肯定比判断库存效率高
//         int updateCount = skTokenMapper.decrease(date, trainCode, 1);
//         return updateCount > 0;
    }

}
