package com.example.config;

import com.example.service.impl.TongYiClient;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class TongYiConfig {

    @Value("${tongyi.api.key}")
    private String apiKey;

    @Value("${tongyi.stream.api.url}")
    private String endpoint;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public TongYiClient tongYiClient(OkHttpClient okHttpClient) {
        return new TongYiClient(okHttpClient, endpoint, apiKey);
    }
}