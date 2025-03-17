--The script to initialize the schema was sourced from the Spring Batch Core dependency: org.springframework.batch.core.

CREATE SCHEMA document_schema;
SET SCHEMA 'document_schema';

CREATE TABLE document_schema.documents (
    id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    document_name VARCHAR(255) NOT NULL,
    minio_path TEXT NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE document_schema.document_tags (
    document_id BIGINT NOT NULL,
    tag TEXT,
    CONSTRAINT fk_document FOREIGN KEY (document_id) REFERENCES document_schema.documents(id) ON DELETE CASCADE
);
