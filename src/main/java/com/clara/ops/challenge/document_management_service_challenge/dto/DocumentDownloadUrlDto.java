package com.clara.ops.challenge.document_management_service_challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DocumentDownloadUrlDto {
  private String url;
}
