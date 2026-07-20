package com.macro.mall.service;

import com.macro.mall.model.PmsProduct;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/** 商品浏览行为统计与 Redis ZSet 热度排行榜。 */
@Service
public class ProductEngagementService {
    private static final String RANK_KEY = "easygoing:shop:product-rank";
    private static final String DAILY_VIEW_KEY_PREFIX = "easygoing:shop:views:";
    private final StringRedisTemplate redisTemplate;
    private final PmsProductService productService;
    private final HotProductCacheService hotProductCacheService;

    public ProductEngagementService(StringRedisTemplate redisTemplate, PmsProductService productService,
                                    HotProductCacheService hotProductCacheService) {
        this.redisTemplate = redisTemplate;
        this.productService = productService;
        this.hotProductCacheService = hotProductCacheService;
    }

    public void recordView(Long productId) {
        if (productId == null) return;
        try {
            redisTemplate.opsForZSet().incrementScore(RANK_KEY, productId.toString(), 1D);
            redisTemplate.expire(RANK_KEY, 30, TimeUnit.DAYS);
            String dailyKey = DAILY_VIEW_KEY_PREFIX + LocalDate.now();
            redisTemplate.opsForHash().increment(dailyKey, productId.toString(), 1L);
            redisTemplate.expire(dailyKey, 14, TimeUnit.DAYS);
        } catch (DataAccessException ignored) {
            // Redis 不可用时仅跳过埋点，不阻断商品详情打开。
        }
    }

    public List<PmsProduct> getRanking(int limit) {
        try {
            Set<String> ids = redisTemplate.opsForZSet().reverseRange(RANK_KEY, 0, Math.max(0, limit - 1));
            if (ids != null && !ids.isEmpty()) {
                Map<Long, PmsProduct> products = productService.list("").stream()
                        .filter(product -> Integer.valueOf(1).equals(product.getPublishStatus()))
                        .collect(java.util.stream.Collectors.toMap(PmsProduct::getId, Function.identity()));
                List<PmsProduct> ranked = ids.stream().map(Long::valueOf).map(products::get)
                        .filter(java.util.Objects::nonNull).toList();
                if (!ranked.isEmpty()) return ranked;
            }
        } catch (DataAccessException ignored) {
            // 降级至推荐/新品缓存。
        }
        return hotProductCacheService.getHotProducts().stream()
                .sorted(Comparator.comparing(PmsProduct::getRecommandStatus, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit).toList();
    }
}
