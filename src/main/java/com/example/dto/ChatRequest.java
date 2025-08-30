package com.example.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ChatRequest {
    private String userId;
    private String conversationId;
    private String content;
    private String model = "qwen-turbo";
    private List<String> files;
    private List<Map<String, String>> history;
}