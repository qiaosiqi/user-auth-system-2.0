package com.authority.backend.service;

import com.authority.backend.dto.DeepseekChatRequest;
import com.authority.backend.dto.DeepseekChatResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class DeepseekService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    @Autowired
    private RestTemplate restTemplate;

    public String generateResponse(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return "用户输入内容为空";
        }

        // 1. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); // 传递 API Key

        // 2. 创建请求体
        DeepseekChatRequest requestBody = new DeepseekChatRequest(prompt);
        HttpEntity<DeepseekChatRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 3. 发送 POST 请求
            ResponseEntity<DeepseekChatResponse> response = restTemplate.postForEntity(
                    API_URL,
                    entity,
                    DeepseekChatResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                DeepseekChatResponse body = response.getBody();
                if (body.getChoices() != null && !body.getChoices().isEmpty()) {
                    // 4. 解析结果
                    return body.getChoices().get(0).getMessage().getContent();
                }
            }
            return "调用 DeepSeek API 失败，状态码：" + response.getStatusCode();
        } catch (HttpClientErrorException e) {
            // 捕获 HTTP 4xx 错误
            return "API 请求失败，状态码: " + e.getStatusCode() + "，请检查 Key 或 URL。";
        } catch (Exception e) {
            e.printStackTrace();
            return "调用 DeepSeek API 异常：" + e.getMessage();
        }
    }
}