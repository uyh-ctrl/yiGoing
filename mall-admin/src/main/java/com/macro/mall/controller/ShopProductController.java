package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.service.HotProductCacheService;
import com.macro.mall.service.ProductEngagementService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 面向消费者的商品读取接口，不复用后台管理查询。 */
@RestController
@RequestMapping("/shop/products")
public class ShopProductController {
    private final HotProductCacheService hotProductCacheService;
    private final ProductEngagementService productEngagementService;

    public ShopProductController(HotProductCacheService hotProductCacheService, ProductEngagementService productEngagementService) {
        this.hotProductCacheService = hotProductCacheService;
        this.productEngagementService = productEngagementService;
    }

    @GetMapping("/hot")
    public CommonResult<List<PmsProduct>> hotProducts() {
        return CommonResult.success(hotProductCacheService.getHotProducts());
    }

    @GetMapping("/ranking")
    public CommonResult<List<PmsProduct>> ranking() {
        return CommonResult.success(productEngagementService.getRanking(8));
    }

    @PostMapping("/{id}/view")
    public CommonResult<Void> recordView(@PathVariable Long id) {
        productEngagementService.recordView(id);
        return CommonResult.success(null);
    }
}
