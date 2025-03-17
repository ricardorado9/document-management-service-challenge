package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.config.MinioProperties;
import com.clara.ops.challenge.document_management_service_challenge.config.ThreadPoolProperties;
import com.clara.ops.challenge.document_management_service_challenge.dto.*;
import com.clara.ops.challenge.document_management_service_challenge.entity.Document;
import com.clara.ops.challenge.document_management_service_challenge.mapper.DocumentMapper;
import com.clara.ops.challenge.document_management_service_challenge.repository.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.repository.DocumentSpecification;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class DocumentManagementService {

  private final DocumentRepository documentRepository;
  private final DocumentMapper documentMapper;
  private final MinioClient minioClient;
  private final ThreadPoolTaskExecutor executor;
  private final String DOCUMENT_BUCKET = "document-bucket";

  public DocumentManagementService(
      DocumentRepository documentRepository,
      DocumentMapper documentMapper,
      MinioProperties minioProperties,
      ThreadPoolProperties threadPoolProperties)
      throws ServerException,
          InsufficientDataException,
          ErrorResponseException,
          NoSuchAlgorithmException,
          IOException,
          InvalidKeyException,
          InvalidResponseException,
          XmlParserException,
          InternalException {
    this.documentRepository = documentRepository;
    this.documentMapper = documentMapper;
    this.minioClient =
        MinioClient.builder()
            .endpoint(minioProperties.getEndpoint())
            .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
            .build();
    this.executor = new ThreadPoolTaskExecutor();
    this.executor.setCorePoolSize(threadPoolProperties.getPoolSize());
    this.executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
    this.executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
    this.executor.initialize();
    this.validateBucket(DOCUMENT_BUCKET);
  }

  public CompletableFuture<Boolean> uploadDocument(
      UploadDocumentDto uploadDocumentDto, MultipartFile file) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            processUpload(uploadDocumentDto, file);
            return true;
          } catch (Exception e) {
            log.error("Error when uploading document: {}", e.getMessage());
            return false;
          }
        },
        executor);
  }

  public PaginatedDocumentSearchDto searchDocuments(
      Integer pageNumber,
      Integer size,
      List<String> sort,
      DocumentSearchFiltersDto documentSearchFiltersDto) {
    Pageable page = PageRequest.of(pageNumber, size, buildSort(sort));
    Specification<Document> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

    if (!ObjectUtils.isEmpty(documentSearchFiltersDto.getUser())) {
      spec = spec.and(DocumentSpecification.hasUser(documentSearchFiltersDto.getUser()));
    }
    if (!ObjectUtils.isEmpty(documentSearchFiltersDto.getName())) {
      spec = spec.and(DocumentSpecification.hasName(documentSearchFiltersDto.getName()));
    }

    if (!ObjectUtils.isEmpty(documentSearchFiltersDto.getTags())) {
      spec = spec.and(DocumentSpecification.hasTags(documentSearchFiltersDto.getTags()));
    }

    Page<Document> pagedResult = documentRepository.findAll(spec, page);

    PaginatedDocumentSearchDto result = new PaginatedDocumentSearchDto();
    result.setDocumentDtos(
        pagedResult.getContent().stream().map(documentMapper::entityToDto).toList());
    result.setMetadataDto(
        MetadataDto.builder()
            .currentPage(pagedResult.getNumber())
            .itemsPerPage(pagedResult.getSize())
            .currentItems(pagedResult.getNumberOfElements())
            .totalItems(pagedResult.getTotalElements())
            .totalPages(pagedResult.getTotalPages())
            .build());

    return result;
  }

    public DocumentDownloadUrlDto generateDownloadUrl(String documentId) {
        DocumentDownloadUrlDto documentDownloadUrlDto = new DocumentDownloadUrlDto();
        documentRepository.findById(Long.valueOf(documentId)).ifPresentOrElse(
                document -> {
                    try {
                        documentDownloadUrlDto.setUrl(
                                minioClient.getPresignedObjectUrl(
                                        GetPresignedObjectUrlArgs.builder()
                                                .method(Method.GET)
                                                .bucket(DOCUMENT_BUCKET)
                                                .object(document.getMinioPath())
                                                .expiry(7, TimeUnit.DAYS)
                                                .build()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    throw new RuntimeException("Document not found");
                });
        return documentDownloadUrlDto;

    }

  private void validateFile(MultipartFile file) {
    if (Objects.isNull(file)) {
      throw new RuntimeException("File is Empty");
    }

    if (!file.getContentType().endsWith("pdf")) {
      throw new RuntimeException("Incorrect file format");
    }
    if (file.getSize() > 500 * 1024 * 1024) {
      throw new RuntimeException("Size exceeds the limit of 500MB");
    }
  }

  private void validateBucket(String bucketName)
      throws ServerException,
          InsufficientDataException,
          ErrorResponseException,
          NoSuchAlgorithmException,
          InvalidKeyException,
          InvalidResponseException,
          XmlParserException,
          InternalException,
          IOException {
    boolean bucketExists =
        minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    if (!bucketExists) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
    }
  }

  private void uploadToMinIo(MultipartFile file, String username)
      throws IOException,
          ServerException,
          InsufficientDataException,
          ErrorResponseException,
          NoSuchAlgorithmException,
          InvalidKeyException,
          InvalidResponseException,
          XmlParserException,
          InternalException {

    String fileName = file.getOriginalFilename();
    // validateBucket(DOCUMENT_BUCKET);
    minioClient.putObject(
        PutObjectArgs.builder()
            .bucket(DOCUMENT_BUCKET)
            .object(username.concat("/").concat(fileName))
            .stream(file.getInputStream(), file.getSize(), -1)
            .contentType(file.getContentType())
            .build());
  }

  private void processUpload(UploadDocumentDto uploadDocumentDto, MultipartFile file)
      throws ServerException,
          InsufficientDataException,
          ErrorResponseException,
          IOException,
          NoSuchAlgorithmException,
          InvalidKeyException,
          InvalidResponseException,
          XmlParserException,
          InternalException {
    validateFile(file);
    Document document = documentMapper.dtoToEntity(uploadDocumentDto);
    document.setDocumentName(file.getOriginalFilename());
    document.setFileType(file.getContentType());
    document.setFileSize(file.getSize());
    document.setMinioPath(document.getUserName().concat("/").concat(file.getOriginalFilename()));
    uploadToMinIo(file, uploadDocumentDto.getUser());
    documentRepository.save(document);
  }

  private Sort buildSort(List<String> sortParams) {
    List<Sort.Order> orders = new ArrayList<>();
    if (CollectionUtils.isEmpty(sortParams)) {
      orders.add(new Sort.Order(Sort.Direction.DESC, "createdAt"));
    } else {
      sortParams.forEach(
          criteria -> {
            String[] parts = criteria.split(",");
            Sort.Direction direction = Sort.Direction.fromString(parts[1].trim());
            orders.add(new Sort.Order(direction, parts[0].trim()));
          });
    }
    return Sort.by(orders);
  }
}
