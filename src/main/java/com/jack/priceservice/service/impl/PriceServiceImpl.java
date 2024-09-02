package com.jack.priceservice.service.impl;

import com.jack.priceservice.schedule.ScheduledTasks;
import com.jack.priceservice.service.PriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PriceServiceImpl implements PriceService {

    private static final Logger logger = LoggerFactory.getLogger(PriceServiceImpl.class);
    public static final String REDIS_KEY = "BTC_CURRENT_PRICE";

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${initial.price:100}")
    private int initialPrice;

    public PriceServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public int getPrice() {
        logger.info("Fetching current BTC price from Redis with key: {}", REDIS_KEY);
        Integer price = (Integer) redisTemplate.opsForValue().get(REDIS_KEY);

        if (price != null) {
            logger.info("Current BTC price retrieved from Redis: {}", price);
        } else {
            logger.warn("BTC price not found in Redis. Falling back to initial price: {}", initialPrice);
        }

        return price != null ? price : initialPrice;
    }

    @Override
    public void setPrice(int price) {
        logger.info("Setting current BTC price in Redis with key: {} to value: {}", REDIS_KEY, price);
        redisTemplate.opsForValue().set(REDIS_KEY, price, Duration.ofMillis(ScheduledTasks.SCHEDULE_RATE_MS));
        logger.info("BTC price set successfully in Redis.");
    }
}
