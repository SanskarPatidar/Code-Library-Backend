package com.sanskar.Code.Library.Backend.repository.version;

import com.sanskar.Code.Library.Backend.model.Version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VersionRepository extends MongoRepository<Version, String> {
    Page<Version> findByBranchIdOrderByVersionDesc(String branchId, Pageable pageable);
    List<Version> findAllBySnippetIdAndDeletedFalse(String snippetId);
    List<Version> findAllByBranchId(String branchId);
    Optional<Version> findTopByBranchIdOrderByVersionDesc(String branchId);
    Optional<Version> findByIdAndDeletedFalse(String id);
}
