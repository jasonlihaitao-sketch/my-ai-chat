package com.example.service;

public interface AiModelService {
    /**
     * 处理模型请求
     * @param request 请求数据
     * @return 响应结果
     */
    Object processRequest(Object request);

    // 同步处理
    Object streamRequest(Object request);   // 流式处理
}