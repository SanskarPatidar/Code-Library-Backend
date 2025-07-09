package com.sanskar.Code.Library.Backend.repository.snippetpushrequest;

import com.sanskar.Code.Library.Backend.model.SnippetPushRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SnippetPushRequestRepositoryCustom {
    Page<SnippetPushRequest> findBySnippetIdValid(String snippetId, Pageable pageable);
}
