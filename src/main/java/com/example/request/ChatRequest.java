package com.example.request;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequest {
    private String model = "qwen-max";
    private List<Message> messages;
    private Boolean stream = true;
    private Double temperature = 0.8;
    private Integer maxTokens = 2000;

    @Data
    public static class Message {
        private String role;
        private String content;

        public Message() {}

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}