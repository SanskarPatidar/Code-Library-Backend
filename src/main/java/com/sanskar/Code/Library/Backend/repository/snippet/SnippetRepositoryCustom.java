package com.sanskar.Code.Library.Backend.repository.snippet;

import com.sanskar.Code.Library.Backend.model.Snippet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SnippetRepositoryCustom {
    Page<Snippet> findByCollaboratorIdAndDeletedFalse(String userId, Pageable pageable);
}
