package hackathon.kb.chakchak.global.httpclient;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class HttpClientConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OkHttpClient okHttpClient() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(64);
        dispatcher.setMaxRequestsPerHost(16);

        ConnectionPool pool = new ConnectionPool(10, 5, TimeUnit.MINUTES);

        return new OkHttpClient.Builder()
                // 타임아웃 상향
                .connectTimeout(java.time.Duration.ofSeconds(20))
                .readTimeout(java.time.Duration.ofSeconds(120))   // 헤더/바디 수신 대기
                .writeTimeout(java.time.Duration.ofSeconds(120))
                .callTimeout(java.time.Duration.ofSeconds(180))   // 전체 호출 상한

                // HTTP/2 keepalive
                .pingInterval(java.time.Duration.ofSeconds(15))

                // 커넥션 풀/재시도
                .connectionPool(pool)
                .retryOnConnectionFailure(true)

                // 과도 동시호출 방지
                .dispatcher(dispatcher)

                .build();
    }

}
