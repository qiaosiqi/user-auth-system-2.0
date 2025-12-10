package com.authority.backend.controller;

import com.authority.backend.service.DeepseekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 智能体问答接口控制器 (DeepSeek)
 */
@RestController
@RequestMapping("/api/deepseek")
public class DeepseekController {

    @Autowired
    private DeepseekService deepseekService;

    /**
     * POST /api/deepseek/chat
     * 接收用户提问，返回模型回答
     * 请求体格式：{"prompt": "你的问题"}
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> params) {
        String prompt = params.get("prompt");
        Map<String, Object> result = new HashMap<>();

        if (prompt == null || prompt.trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "提问内容不能为空");
            return result;
        }

        String responseContent = deepseekService.generateResponse(prompt);

        result.put("code", 200);
        result.put("msg", "调用成功");
        result.put("data", responseContent); // 返回模型回答

        return result;
    }
}