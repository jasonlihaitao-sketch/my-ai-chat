package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class ModelFactory {

    private Map<String, AiModelService> modelServices = new HashMap<>();

    @Autowired
    private StreamResponseService streamResponseService;
    @Autowired
    private OpenAIService openAIService;

    @PostConstruct
    public void init() {
//        modelServices.put("openai", openAIService);
//        modelServices.put("stream", streamResponseService);
    }

    public AiModelService getModelService(String modelCode) {
        String serviceKey = modelCode.split("-")[0]; // 从模型代码中提取服务标识
        AiModelService service = modelServices.get(serviceKey);
        if (service == null) {
            throw new IllegalArgumentException("Unsupported model: " + modelCode);
        }
        return service;
    }
}