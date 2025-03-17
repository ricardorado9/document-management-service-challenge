package com.clara.ops.challenge.document_management_service_challenge.controller;

import com.clara.ops.challenge.document_management_service_challenge.dto.DocumentDownloadUrlDto;
import com.clara.ops.challenge.document_management_service_challenge.dto.DocumentSearchFiltersDto;
import com.clara.ops.challenge.document_management_service_challenge.dto.PaginatedDocumentSearchDto;
import com.clara.ops.challenge.document_management_service_challenge.dto.UploadDocumentDto;
import com.clara.ops.challenge.document_management_service_challenge.service.DocumentManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/document-management")
@AllArgsConstructor
public class DocumentManagementController {

  private final DocumentManagementService documentManagementService;
  private final ObjectMapper objectMapper;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  private ResponseEntity<Void> uploadDocument(
      @RequestPart("file") MultipartFile file, @RequestPart("metadata") String metadataJson)
      throws JsonProcessingException, ExecutionException, InterruptedException {

    UploadDocumentDto metadata = objectMapper.readValue(metadataJson, UploadDocumentDto.class);
    CompletableFuture<Boolean> result = documentManagementService.uploadDocument(metadata, file);
    return result.get() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
  }

  @PostMapping("/search")
  private ResponseEntity<PaginatedDocumentSearchDto> searchDocuments(
      @RequestParam Integer page,
      @RequestParam Integer size,
      @RequestParam(required = false) List<String> sort,
      @RequestBody DocumentSearchFiltersDto documentSearchFiltersDto) {
    PaginatedDocumentSearchDto result =
        documentManagementService.searchDocuments(page, size, sort, documentSearchFiltersDto);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/download/{documentId}")
  private ResponseEntity<DocumentDownloadUrlDto> downloadDocument(@PathVariable String documentId) {
    return ResponseEntity.ok(documentManagementService.generateDownloadUrl(documentId));


  }
}
