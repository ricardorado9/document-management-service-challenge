package com.clara.ops.challenge.document_management_service_challenge.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Table(name = "documents", schema = "document_schema")
@Entity
@Getter
@Setter
public class Document {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_name", nullable = false)
  private String userName;

  @Column(name = "document_name", nullable = false)
  private String documentName;

  @ElementCollection
  @CollectionTable(
      name = "document_tags",
      joinColumns = @JoinColumn(name = "document_id"),
      schema = "document_schema")
  @Column(name = "tag")
  private List<String> tags;

  @Column(name = "minio_path", nullable = false)
  private String minioPath;

  @Column(name = "file_size", nullable = false)
  private long fileSize;

  @Column(name = "file_type", nullable = false)
  private String fileType;

  @CreationTimestamp private LocalDateTime createdAt;
}
