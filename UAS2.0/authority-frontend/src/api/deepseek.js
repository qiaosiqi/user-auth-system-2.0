// src/api/deepseek.js

import request from '../api/request'

/**
 * 调用 DeepSeek API 进行智能体问答
 * @param {string} prompt - 用户的提问内容
 * @returns
 */
export function chatWithDeepseek(prompt) {
    return request({
        url: '/api/deepseek/chat', // 对应后端的 Controller 路径
        method: 'post',
        data: { prompt } // 发送 JSON: {"prompt": "..."}
    })
}