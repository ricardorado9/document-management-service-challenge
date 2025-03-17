package com.clara.ops.challenge.document_management_service_challenge.mapper;

import com.clara.ops.challenge.document_management_service_challenge.dto.DocumentDto;
import com.clara.ops.challenge.document_management_service_challenge.dto.UploadDocumentDto;
import com.clara.ops.challenge.document_management_service_challenge.entity.Document;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

  @Mapping(target = "userName", source = "user")
  @Mapping(target = "documentName", source = "name")
  Document dtoToEntity(UploadDocumentDto uploadDocumentDto);

  @Mapping(target = "user", source = "userName")
  @Mapping(target = "name", source = "documentName")
  @Mapping(target = "size", source = "fileSize")
  @Mapping(target = "type", source = "fileType")
  DocumentDto entityToDto(Document uploadDocumentDto);
}
