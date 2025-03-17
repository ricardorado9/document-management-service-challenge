package com.clara.ops.challenge.document_management_service_challenge.dto;

import java.util.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
// @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaginatedDocumentSearchDto {

  private MetadataDto metadataDto;
  private List<DocumentDto> documentDtos;
}
