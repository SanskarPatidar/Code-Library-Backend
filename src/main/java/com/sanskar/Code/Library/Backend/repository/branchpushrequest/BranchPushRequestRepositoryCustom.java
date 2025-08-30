package com.sanskar.Code.Library.Backend.repository.branchpushrequest;

import com.sanskar.Code.Library.Backend.model.BranchPushRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BranchPushRequestRepositoryCustom {
    Page<BranchPushRequest> findAllBySnippetIdAndValidAsPage(String snippetId, Pageable pageable);
    Optional<BranchPushRequest> findByIdValid(String id);
    List<BranchPushRequest> findAllValidBySnippetId(String snippetId);
}
