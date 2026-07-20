package com.macro.mall.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerChatParam(@NotBlank(message = "问题不能为空") String question) {
}
