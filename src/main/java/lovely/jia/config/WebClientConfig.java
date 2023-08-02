package lovely.jia.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${jia.hanyang.base-url}")
    private String baseUri;

    @Bean
    public WebClient webClient(Builder builder){
        WebClient build = builder
                .baseUrl(baseUri)
                .build();
        log.info("BaseUrl ::: {}", baseUri);
        return build;
    }
}
