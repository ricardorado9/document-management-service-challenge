package com.clara.ops.challenge.document_management_service_challenge.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
// @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MetadataDto {

  private Integer currentPage;
  private Integer itemsPerPage;
  private Integer currentItems;
  private Integer totalPages;
  private Long totalItems;
}
