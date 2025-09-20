// src/main/java/com/skthon/manjil/global/ai/OpenAiClient.java
package com.skthon.manjil.global.ai;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class OpenAiClient {

  private final WebClient webClient;
  private final ObjectMapper om = new ObjectMapper();

  // 추가 보관 (디버깅/참조용)
  private final String apiKey; // properties: openai.api.key
  private final String baseUrl; // properties: openai.base-url
  @Nullable private final String projectId; // properties: openai.project-id (없어도 됨)

  // 기존 파라미터 유지
  private final String model;
  private final double temperature;
  private final int maxTokens;
  private final long timeoutMillis;

  /** WebClient.Builder 주입 + 프로젝트 키일 때만 OpenAI-Project 헤더 추가. */
  public OpenAiClient(
      WebClient.Builder builder,
      @Value("${openai.api.key}") String apiKey,
      @Value("${openai.base-url:https://api.openai.com/v1}") String baseUrl,
      @Value("${openai.project-id:}") String projectId,
      @Value("${openai.chat-model}") String model,
      @Value("${openai.temperature:0.2}") double temperature,
      @Value("${openai.max-tokens:500}") int maxTokens,
      @Value("${openai.timeout-millis:10000}") long timeoutMillis) {
    this.apiKey = apiKey;
    this.baseUrl = baseUrl;
    this.projectId = (projectId == null || projectId.isBlank()) ? null : projectId;

    this.model = model;
    this.temperature = temperature;
    this.maxTokens = maxTokens;
    this.timeoutMillis = timeoutMillis;

    this.webClient =
        builder
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(
                (request, next) -> {
                  ClientRequest.Builder mutated = ClientRequest.from(request);
                  boolean isProjectKey = apiKey != null && apiKey.startsWith("sk-proj-");
                  if (isProjectKey
                      && this.projectId != null
                      && this.projectId.startsWith("proj_")) {
                    mutated.header("OpenAI-Project", this.projectId);
                  }
                  return next.exchange(mutated.build());
                })
            .build();
  }

  private static String truncate(String s, int max) {
    if (s == null) return null;
    return s.length() <= max ? s : s.substring(0, max) + "...(truncated)";
  }

  /**
   * Chat Completions(JSON mode) 호출.
   *
   * @param system 시스템 프롬프트
   * @param user 유저 프롬프트
   * @return OpenAI의 원본 문자열(보통 JSON)
   */
  public String chatCompletionJson(String system, String user) {
    try {
      Map<String, Object> body = new HashMap<>();
      body.put("model", model);
      body.put("temperature", temperature);
      body.put("max_tokens", maxTokens);
      body.put("response_format", Map.of("type", "json_object"));
      body.put(
          "messages",
          List.of(
              Map.of("role", "system", "content", system),
              Map.of("role", "user", "content", user)));

      log.info("[OpenAI] request model={}, timeout={}ms", model, timeoutMillis);
      log.debug("[OpenAI] request body={}", truncate(om.writeValueAsString(body), 2000));

      // 상태/헤더/바디를 모두 보기 위해 toEntity 사용
      var entity =
          webClient
              .post()
              .uri("/chat/completions")
              .accept(MediaType.APPLICATION_JSON)
              .bodyValue(body)
              .retrieve()
              .onStatus(s -> s.value() == 401 || s.value() == 403, this::authError)
              .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(), this::httpError)
              .toEntity(String.class)
              .timeout(Duration.ofMillis(timeoutMillis))
              .block();

      if (entity == null) {
        log.warn("[OpenAI] response entity is null");
        throw new RuntimeException("openai-null-response");
      }

      log.info("[OpenAI] status={}, headers={}", entity.getStatusCode(), entity.getHeaders());
      String raw = entity.getBody();
      log.info("[OpenAI] raw body: {}", truncate(raw, 2000));

      return raw;

    } catch (Exception e) {
      log.warn("OpenAI call failed: {}", e.toString());
      throw new RuntimeException("openai-call-failed", e);
    }
  }

  // 401/403 상세 로그
  private Mono<? extends Throwable> authError(ClientResponse resp) {
    return resp.bodyToMono(String.class)
        .defaultIfEmpty("")
        .flatMap(
            body -> {
              boolean isProjectKey = apiKey != null && apiKey.startsWith("sk-proj-");
              String reason =
                  "Unauthorized(401/403). 프로젝트 키 사용 시 projectId(proj_…) 헤더가 필요한지 확인하세요. "
                      + "현재 apiKey(prefix)="
                      + (isProjectKey ? "sk-proj-" : "sk-?")
                      + ", projectId="
                      + (StringUtils.hasText(projectId) ? projectId : "<null>")
                      + ", baseUrl="
                      + baseUrl;
              log.warn("OpenAI auth error {}: {}", resp.statusCode(), body);
              return Mono.error(new RuntimeException(reason));
            });
  }

  // 기타 4xx/5xx
  private Mono<? extends Throwable> httpError(ClientResponse resp) {
    return resp.bodyToMono(String.class)
        .defaultIfEmpty("")
        .flatMap(
            body -> {
              log.warn("OpenAI HTTP error {}: {}", resp.statusCode(), body);
              return Mono.error(new RuntimeException("openai-http-error: " + resp.statusCode()));
            });
  }
}
