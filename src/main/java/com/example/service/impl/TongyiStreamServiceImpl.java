package com.example.service.impl;

import com.example.dto.ChatRequest;
import com.example.service.StreamResponseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class TongyiStreamServiceImpl implements StreamResponseService {

    @Value("${tongyi.api.key}")
    private String apiKey;

    @Value("${tongyi.stream.api.url}")
    private String streamApiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public SseEmitter streamChat(ChatRequest request, String conversationId) {
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L); // 5分钟超时

        executor.execute(() -> {
            try {
                Map<String, Object> payload = new HashMap<>();
                Map<String, Object> input = new HashMap<>();
                Map<String, Object> parameters = new HashMap<>();

                input.put("messages", request.getHistory());
                parameters.put("stream", true);
                parameters.put("incremental_output", true);

                payload.put("model", request.getModel());
                payload.put("input", input);
                payload.put("parameters", parameters);

                String jsonPayload = objectMapper.writeValueAsString(payload);

                try (CloseableHttpClient client = HttpClients.createDefault()) {
                    HttpPost httpPost = new HttpPost(streamApiUrl);
                    httpPost.setHeader("Content-Type", "application/json");
                    httpPost.setHeader("Authorization", "Bearer " + apiKey);
                    httpPost.setEntity(new StringEntity(jsonPayload, "UTF-8"));

                    try (CloseableHttpResponse response = client.execute(httpPost);
                         BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data: ")) {
                                String data = line.substring(6);
                                if ("[DONE]".equals(data)) {
                                    emitter.complete();
                                    break;
                                }

                                // 解析并发送数据
                                Map<String, Object> result = objectMapper.readValue(data, Map.class);
                                emitter.send(result);
                                // 打印每段文本内容
                                if (result.containsKey("output") && result.get("output") instanceof Map) {
                                    Map<String, Object> output = (Map<String, Object>) result.get("output");
                                    if (output.containsKey("text")) {
                                        String text = (String) output.get("text");
                                        log.info("Received text content: {}", text);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error in streaming response", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}