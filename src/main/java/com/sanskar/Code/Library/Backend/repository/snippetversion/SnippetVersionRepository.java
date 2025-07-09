package com.sanskar.Code.Library.Backend.repository.snippetversion;

import com.sanskar.Code.Library.Backend.model.SnippetVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnippetVersionRepository extends MongoRepository<SnippetVersion, String> {
    Page<SnippetVersion> findBySnippetIdOrderByVersionDesc(String snippetId, Pageable pageable);
}
