package com.clara.ops.challenge.document_management_service_challenge.dto;

import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DocumentSearchFiltersDto {

  private String user;
  private String name;
  private List<String> tags;
}
