package com.skthon.manjil.infra.redis;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisUtil {

  private final StringRedisTemplate template;

  // 데이터 가져오기
  public String getData(String key) {
    ValueOperations<String, String> valueOperations = template.opsForValue();
    return valueOperations.get(key);
  }

  // 데이터 존재 여부
  public boolean existData(String key) {
    return Boolean.TRUE.equals(template.hasKey(key));
  }

  // 데이터 생성
  public void setData(String key, String value) {
    ValueOperations<String, String> valueOperations = template.opsForValue();
    valueOperations.set(key, value);
  }

  // 데이터 생성 및 파기시간
  public void setData(String key, String value, long duration) {
    ValueOperations<String, String> valueOperations = template.opsForValue();
    Duration expireDuration = Duration.ofSeconds(duration);
    valueOperations.set(key, value, expireDuration);
  }

  // 데이터 삭제
  public void deleteData(String key) {
    template.delete(key);
  }

  // 데이터 증가
  public Long increment(String key) {
    return template.opsForValue().increment(key);
  }

  // 데이터 감소
  public Long decrement(String key) {
    return template.opsForValue().decrement(key);
  }

  // 데이터(숫자) 가져오기
  public long getLongValue(String key) {
    String value = getData(key);
    return value != null ? Long.parseLong(value) : 0L;
  }
}
