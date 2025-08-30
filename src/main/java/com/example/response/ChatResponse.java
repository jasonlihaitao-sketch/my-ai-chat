package com.example.response;

import lombok.Data;

import java.util.List;

@Data
public class ChatResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Delta delta;
        private Integer index;
        private String finishReason;

        @Data
        public static class Delta {
            private String role;
            private String content;
        }
    }
}