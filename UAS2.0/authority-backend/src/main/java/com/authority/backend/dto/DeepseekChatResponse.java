package com.authority.backend.dto;

import java.util.List;
import lombok.Data;

/**
 * DeepSeek API 响应体结构 (关键部分)
 */
@Data
public class DeepseekChatResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        // 使用 DeepseekChatRequest 中定义的 Message 内部类
        private DeepseekChatRequest.Message message;
        private String finish_reason;
        private int index;
    }
}