package com.macro.mall.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.model.PmsProduct;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** 消费者首页热点商品：缓存旁路，Redis 不可用时自动回源数据库。 */
@Service
public class HotProductCacheService {
    private static final String HOT_PRODUCTS_KEY = "easygoing:shop:hot-products:v1";
    private static final long CACHE_SECONDS = 300;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final PmsProductService productService;

    public HotProductCacheService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, PmsProductService productService) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.productService = productService;
    }

    public List<PmsProduct> getHotProducts() {
        try {
            String cached = redisTemplate.opsForValue().get(HOT_PRODUCTS_KEY);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<List<PmsProduct>>() {});
            }
        } catch (DataAccessException | com.fasterxml.jackson.core.JsonProcessingException ignored) {
            // 开发环境未启动 Redis 或缓存损坏时，继续从数据库读取。
        }

        List<PmsProduct> products = productService.list("").stream()
                .filter(item -> Integer.valueOf(1).equals(item.getPublishStatus()))
                .sorted(Comparator.comparing(PmsProduct::getRecommandStatus, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(PmsProduct::getNewStatus, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(PmsProduct::getId))
                .limit(8)
                .toList();
        try {
            redisTemplate.opsForValue().set(HOT_PRODUCTS_KEY, objectMapper.writeValueAsString(products), CACHE_SECONDS, TimeUnit.SECONDS);
        } catch (DataAccessException | com.fasterxml.jackson.core.JsonProcessingException ignored) {
            // 缓存写入失败不影响商品浏览。
        }
        return products;
    }

    public void evict() {
        try { redisTemplate.delete(HOT_PRODUCTS_KEY); } catch (DataAccessException ignored) { }
    }
}
