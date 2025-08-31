package com.sanskar.Code.Library.Backend.repository.branch;

import com.sanskar.Code.Library.Backend.model.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends MongoRepository<Branch, String> {
    List<Branch> findAllBySnippetIdAndDeletedFalse(String snippetId);
    Page<Branch> findAllBySnippetIdAndDeletedFalse(String snippetId, Pageable pageable);
    List<Branch> findAllBySourceBranchId(String sourceBranchId);
    Optional<Branch> findByIdAndDeletedFalse(String id);
    boolean existsBySnippetIdAndBranchName(String snippetId, String branchName);
}
