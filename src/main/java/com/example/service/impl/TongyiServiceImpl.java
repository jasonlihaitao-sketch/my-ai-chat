package com.example.service.impl;

import com.example.service.AiModelService;
import org.springframework.stereotype.Service;

@Service("tongyiService")
public class TongyiServiceImpl implements AiModelService {
    @Override
    public Object processRequest(Object request) {
        // 实现通义千问的逻辑
        return "Tongyi Service Response";
    }

    @Override
    public Object streamRequest(Object request) {
        return null;
    }
}