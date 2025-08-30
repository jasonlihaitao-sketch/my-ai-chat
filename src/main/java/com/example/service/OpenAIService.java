package com.example.service;

public interface OpenAIService {
    /**
     * 处理 OpenAI 请求
     * @param request 请求数据
     * @return 响应结果
     */
    Object processRequest(Object request);

    /**
     * 流式处理 OpenAI 请求
     * @param request 请求数据
     * @return 流式响应
     */
    Object streamRequest(Object request);
}
