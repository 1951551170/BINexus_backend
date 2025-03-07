package com.BINexus.back.manager;

import com.BINexus.back.common.ErrorCode;
import com.BINexus.back.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供 RedisLimiter 限流基础服务的（提供了通用的能力）
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     *
     * @param key 区分不同的限流器，比如
     */
    public void doRateLimit(String key) {
        // 根据key创建限流器，每个key有单独的限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //限流器规则
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }

        /**
         * rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
         * RateType.OVERALL: 表示此限流器的类型为整体限流。这意味着指定的速率限制适用于所有请求的总和，而不是每个单独的请求源。也就是说限制同一个用户所有的请求。
         * 2: 表示在给定的时间间隔内允许的最大请求数量。在这个例子中，表示每秒最多允许2个请求。
         * 1: 表示每个时间间隔内的令牌桶大小，即最大并发请求数。在这个例子中，最多允许1个突发请求。也就是一共3个。
         * RateIntervalUnit.SECONDS: 指定时间间隔单位为秒。
         */
    }

}
