package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.CustomerChatParam;
import com.macro.mall.dto.CustomerChatResult;
import com.macro.mall.service.CustomerRagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/customer-service")
public class CustomerServiceController {
    private final ObjectProvider<CustomerRagService> customerRagService;

    public CustomerServiceController(ObjectProvider<CustomerRagService> customerRagService) {
        this.customerRagService = customerRagService;
    }

    @PostMapping("/chat")
    public CommonResult<CustomerChatResult> chat(@Valid @RequestBody CustomerChatParam request) {
        CustomerRagService service = customerRagService.getIfAvailable();
        if (service == null) {
            return CommonResult.failed("智能客服尚未配置。请设置 EASYGOING_AI_BASE_URL、EASYGOING_AI_API_KEY 并启用 easygoing.ai.enabled。");
        }
        return CommonResult.success(service.answer(request.question()));
    }
}
