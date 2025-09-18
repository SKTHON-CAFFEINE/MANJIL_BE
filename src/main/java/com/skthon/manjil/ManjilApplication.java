package com.skthon.manjil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ManjilApplication {

  public static void main(String[] args) {
    SpringApplication.run(ManjilApplication.class, args);
  }
}
