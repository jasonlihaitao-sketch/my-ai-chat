import com.example.dto.ChatRequest;
import com.example.service.impl.TongyiStreamServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TongyiStreamServiceImplTest {

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @Mock
    private BufferedReader bufferedReader;

    @Mock
    private ExecutorService executorService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TongyiStreamServiceImpl tongyiStreamService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(tongyiStreamService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(tongyiStreamService, "streamApiUrl", "http://test-api-url");
    }

    @Test
    public void testStreamChat_Success() throws Exception {
        // Mock request
        ChatRequest request = new ChatRequest();
        request.setModel("qwen-turbo");
//        request.setHistory(List.of(Map.of("role", "user", "content", "Hello")));

        // Mock HTTP response
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
        when(httpResponse.getEntity().getContent()).thenReturn(mock(java.io.InputStream.class));
        when(bufferedReader.readLine())
                .thenReturn("data: {\"response\": \"Hello, how can I help you?\"}")
                .thenReturn("data: [DONE]")
                .thenReturn(null);

        // Mock ObjectMapper
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("response", "Hello, how can I help you?");
        when(objectMapper.readValue(any(String.class), any(Class.class))).thenReturn(mockResult);

        // Execute
        SseEmitter emitter = tongyiStreamService.streamChat(request, "test-conversation-id");

        // Verify
        assertNotNull(emitter);
        verify(httpClient, times(1)).execute(any(HttpPost.class));
    }

    @Test
    public void testStreamChat_Error() throws Exception {
        // Mock request
        ChatRequest request = new ChatRequest();
        request.setModel("qwen-turbo");
//        request.setHistory(List.of(Map.of("role", "user", "content", "Hello")));

        // Mock HTTP error
        when(httpClient.execute(any(HttpPost.class))).thenThrow(new RuntimeException("API Error"));

        // Execute
        SseEmitter emitter = tongyiStreamService.streamChat(request, "test-conversation-id");

        // Verify
        assertNotNull(emitter);
        verify(httpClient, times(1)).execute(any(HttpPost.class));
    }
}