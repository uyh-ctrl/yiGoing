package com.macro.mall.dto;

import java.util.List;

public record CustomerChatResult(String answer, List<String> sources) {
}
