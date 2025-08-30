package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.models")
public class ModelConfig {
    private Map<String, ModelProperties> config;

    @Data
    public static class ModelProperties {
        private String apiUrl;
        private String streamApiUrl;
        private String apiKey;
        private Integer maxTokens;
        private Double temperature;
        private String modelName;
    }
}