package com.clara.ops.challenge.document_management_service_challenge.repository;

import com.clara.ops.challenge.document_management_service_challenge.entity.Document;
import jakarta.persistence.criteria.Join;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

public class DocumentSpecification {

  public static Specification<Document> hasUser(String userName) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(root.get("userName"), "%" + userName + "%");
  }

  public static Specification<Document> hasName(String documentName) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(root.get("documentName"), "%" + documentName + "%");
  }

  public static Specification<Document> hasTags(List<String> tags) {
    return (root, query, criteriaBuilder) -> {
      if (CollectionUtils.isEmpty(tags)) {
        return criteriaBuilder.conjunction();
      }
      Join<Document, String> tagsJoin = root.join("tags");
      return tagsJoin.in(tags);
    };
  }
}
