package com.sanskar.Code.Library.Backend.repository.snippetpushrequest;

import com.sanskar.Code.Library.Backend.model.SnippetPushRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnippetPushRequestRepository extends MongoRepository<SnippetPushRequest, String>, SnippetPushRequestRepositoryCustom {
    Page<SnippetPushRequest> findByRequesterUsernameOrderByRequestedAtDesc(String requesterUsername, Pageable pageable);
}
