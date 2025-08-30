import com.example.exception.TongYiException;
import com.example.request.ChatRequest;
import com.example.service.impl.TongYiClient;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TongYiClientTest {

    private TongYiClient tongYiClient;
    private OkHttpClient httpClient;
    private String endpoint = "https://api.example.com";
    private String apiKey = "test-api-key";

    @BeforeEach
    void setUp() {
        httpClient = mock(OkHttpClient.class);
        tongYiClient = new TongYiClient(httpClient, endpoint, apiKey);
    }

    @Test
    void testBuildRequest() throws Exception {
        ChatRequest request = new ChatRequest();
        request.setMessages(Collections.singletonList(new ChatRequest.Message("user", "Hello")));

        Request httpRequest = tongYiClient.buildRequest(request);

        assertEquals(endpoint + "/v1/chat/completions", httpRequest.url().toString());
        assertEquals("POST", httpRequest.method());
        assertNotNull(httpRequest.header("Authorization"));
        assertNotNull(httpRequest.header("Content-Type"));
        assertNotNull(httpRequest.header("X-Request-Id"));
    }

    @Test
    void testStreamChat_Success() throws Exception {
        ChatRequest request = new ChatRequest();
        request.setMessages(Collections.singletonList(new ChatRequest.Message("user", "Hello")));

        Response mockResponse = mock(Response.class);
        ResponseBody mockBody = mock(ResponseBody.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockBody);
        when(mockBody.byteStream()).thenReturn(getClass().getResourceAsStream("/stream_response.txt"));

        Call mockCall = mock(Call.class);
        when(httpClient.newCall(any())).thenReturn(mockCall);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(mockCall, mockResponse);
            return null;
        }).when(mockCall).enqueue(any());

        Flux<String> result = tongYiClient.streamChat(request.getMessages());

        StepVerifier.create(result)
                .expectNext("Response content 1")
                .expectNext("Response content 2")
                .verifyComplete();
    }

    @Test
    void testStreamChat_NetworkError() {
        ChatRequest request = new ChatRequest();
        request.setMessages(Collections.singletonList(new ChatRequest.Message("user", "Hello")));

        Call mockCall = mock(Call.class);
        when(httpClient.newCall(any())).thenReturn(mockCall);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(mockCall, new IOException("Network error"));
            return null;
        }).when(mockCall).enqueue(any());

        Flux<String> result = tongYiClient.streamChat(request.getMessages());

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof TongYiException &&
                        "NetworkError".equals(((TongYiException) throwable).getErrorCode()))
                .verify();
    }

    @Test
    void testStreamChat_ApiError() throws Exception {
        ChatRequest request = new ChatRequest();
        request.setMessages(Collections.singletonList(new ChatRequest.Message("user", "Hello")));

        Response mockResponse = mock(Response.class);
        ResponseBody mockBody = mock(ResponseBody.class);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockResponse.code()).thenReturn(400);
        when(mockResponse.body()).thenReturn(mockBody);
        when(mockBody.string()).thenReturn("{\"error\": {\"code\": \"InvalidParameter\", \"message\": \"Invalid input\"}}");

        Call mockCall = mock(Call.class);
        when(httpClient.newCall(any())).thenReturn(mockCall);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(mockCall, mockResponse);
            return null;
        }).when(mockCall).enqueue(any());

        Flux<String> result = tongYiClient.streamChat(request.getMessages());

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof TongYiException &&
                        "InvalidParameter".equals(((TongYiException) throwable).getErrorCode()))
                .verify();
    }

    @Test
    void testStreamChatWithRetry() throws Exception {
        ChatRequest request = new ChatRequest();
        request.setMessages(Collections.singletonList(new ChatRequest.Message("user", "Hello")));

        Response mockResponse = mock(Response.class);
        ResponseBody mockBody = mock(ResponseBody.class);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockResponse.code()).thenReturn(429);
        when(mockResponse.body()).thenReturn(mockBody);
        when(mockBody.string()).thenReturn("{\"error\": {\"code\": \"RateLimit\", \"message\": \"Rate limit exceeded\"}}");

        Call mockCall = mock(Call.class);
        when(httpClient.newCall(any())).thenReturn(mockCall);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(mockCall, mockResponse);
            return null;
        }).when(mockCall).enqueue(any());

        Flux<String> result = tongYiClient.streamChatWithRetry(request.getMessages(), 3);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof TongYiException.RateLimitException)
                .verify();
    }
}
