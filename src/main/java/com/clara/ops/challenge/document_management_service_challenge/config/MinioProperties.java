package com.clara.ops.challenge.document_management_service_challenge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperties {
  private String rootUser;
  private String rootPassword;
  private String accessKey;
  private String secretKey;
  private String endpoint;
}
