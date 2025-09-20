package com.skthon.manjil.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {
  private String apiKey;
  private String baseUrl;
  private String chatModel;
  private Integer timeoutMillis;
  private Double temperature;
  private Integer maxTokens;
}
