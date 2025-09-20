package com.skthon.manjil.global.config;

import java.time.Duration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
@RequiredArgsConstructor
public class OpenAiConfig {

  private final OpenAiProperties props;

  @Bean
  public WebClient openAiWebClient() {
    ExchangeStrategies strategies =
        ExchangeStrategies.builder()
            .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
            .build();

    return WebClient.builder()
        .baseUrl(props.getBaseUrl())
        .exchangeStrategies(strategies)
        .defaultHeader("Authorization", "Bearer " + props.getApiKey())
        .defaultHeader("Content-Type", "application/json")
        .build();
  }

  @Bean
  public Duration openAiTimeout() {
    return Duration.ofMillis(props.getTimeoutMillis());
  }
}
