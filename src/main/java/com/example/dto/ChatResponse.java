package com.example.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ChatResponse {
    private String content;
    private String error;
    private Map<String, Object> output;
    private Map<String, Object> usage;
}