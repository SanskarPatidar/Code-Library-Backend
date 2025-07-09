package com.sanskar.Code.Library.Backend.repository.snippet;

import com.sanskar.Code.Library.Backend.model.Snippet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SnippetRepository extends MongoRepository<Snippet, String>, SnippetRepositoryCustom {
    Optional<Snippet> findByIdAndDeletedFalse(String id); // QUERY METHOD DSL
    Page<Snippet> findByPublicVisibilityTrueAndDeletedFalse(Pageable pageable);
    Page<Snippet> findByPublicVisibilityTrueAndDeletedFalseAndTagsIn(List<String> tags, Pageable pageable);
    Page<Snippet> findAllByAuthorNameAndDeletedFalse(String authorName, Pageable pageable);
}
