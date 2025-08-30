package com.example.service.impl;

import com.example.service.OpenAIService;
import org.springframework.stereotype.Service;

@Service
public class OpenAIServiceImpl implements OpenAIService {
    @Override
    public Object processRequest(Object request) {
        // 实现 OpenAI 同步请求逻辑
        return "OpenAI Sync Response: " + request;
    }

    @Override
    public Object streamRequest(Object request) {
        // 实现 OpenAI 流式请求逻辑
        return "OpenAI Stream Response: " + request;
    }
}
