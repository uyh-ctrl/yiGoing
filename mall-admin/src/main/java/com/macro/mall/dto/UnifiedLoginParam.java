package com.macro.mall.dto;

import jakarta.validation.constraints.NotBlank;

public record UnifiedLoginParam(@NotBlank String username, @NotBlank String password) {
}
