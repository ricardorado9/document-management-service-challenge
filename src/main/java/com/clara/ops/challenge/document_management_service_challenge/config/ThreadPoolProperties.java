package com.clara.ops.challenge.document_management_service_challenge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.thread-pool")
@Data
public class ThreadPoolProperties {
  private int poolSize;
  private int maxPoolSize;
  private int queueCapacity;
}
