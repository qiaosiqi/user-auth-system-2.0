package com.authority.backend.dto;

import java.util.List;
import lombok.Data;

/**
 * DeepSeek API 请求体结构
 */
@Data
public class DeepseekChatRequest {

    private String model = "deepseek-chat";
    private List<Message> messages;
    private Double temperature = 0.7;

    public DeepseekChatRequest(String userContent) {
        // 1. 🚨 定义系统预设 System Prompt
        String systemInstruction = buildSystemInstruction();

        // 2. 构建消息列表：系统指令 -> 用户问题
        this.messages = List.of(
                new Message("system", systemInstruction), // 放在第一位
                new Message("user", userContent)
        );
    }

    /**
     * 构建AI的预设和背景信息
     */
    private String buildSystemInstruction() {
        // 🚨 在这里修改你的设定！
        StringBuilder sb = new StringBuilder();

        // --- 核心角色设定 ---
        sb.append("你是一位专业、严谨且乐于助人的中文助手，专注于【权限管理系统】领域。");
        sb.append("你的主要目标是根据用户输入，提供关于该系统功能、配置、安全建议或操作指南的准确回复。\n\n");

        // --- 个人信息设定 ---
        // 假设用户名为 "张三"
        sb.append("我的名字是乔思齐。在回复中请保持礼貌，并记住这个名字。\n\n");

        // --- 系统功能设定 ---
        sb.append("该权限管理系统具备以下核心模块和功能：\n");
        sb.append("1. **用户管理**：创建、编辑、删除用户，分配初始密码。\n");
        sb.append("2. **角色管理**：定义不同的角色（如管理员、普通员工、访客），并设置角色的权限集合。\n");
        sb.append("3. **权限管理**：以树状结构定义系统的所有权限点和菜单项，并与角色关联。\n");
        sb.append("4. **智能体问答**：当前模块，使用DeepSeek大模型提供系统相关支持。\n\n");

        sb.append("请根据以上信息，使用专业的语气回答用户的问题。");

        return sb.toString();
    }

    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}