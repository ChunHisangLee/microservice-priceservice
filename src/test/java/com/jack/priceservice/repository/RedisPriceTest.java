package com.jack.priceservice.repository;

import com.jack.priceservice.config.RedisTestConfig;
import com.jack.priceservice.service.impl.PriceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
@Import(RedisTestConfig.class)
class RedisPriceTest {

    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6-alpine")
            .withExposedPorts(6379);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeEach
    void removeConfiguredKey() {
        redisTemplate.delete(PriceServiceImpl.REDIS_KEY);
    }

    @Test
    void testSavePriceToRedis() {
        Double price = 450.0;
        redisTemplate.opsForValue().set(PriceServiceImpl.REDIS_KEY, price);

        Double redisPrice = (Double) redisTemplate.opsForValue().get(PriceServiceImpl.REDIS_KEY);
        assertThat(redisPrice).isEqualTo(price);
    }

    @Test
    void testRedisKeyDoesNotExist() {
        assertThat(redisTemplate.hasKey(PriceServiceImpl.REDIS_KEY)).isFalse();
    }
}
