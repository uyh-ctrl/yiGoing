package com.macro.mall.dto;

public record UnifiedLoginResult(String accountType, String token, String tokenHead, String displayName, String avatar) {
}
