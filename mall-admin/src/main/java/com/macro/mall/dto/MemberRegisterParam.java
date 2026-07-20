package com.macro.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberRegisterParam(@NotBlank String username, @NotBlank String password,
                                  @Pattern(regexp = "^1\\d{10}$", message = "请输入有效手机号") String phone) {
}
