package com.clara.ops.challenge.document_management_service_challenge.dto;

import java.util.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
// @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UploadDocumentDto {

  private String user;
  private String name;
  private List<String> tags;
}
