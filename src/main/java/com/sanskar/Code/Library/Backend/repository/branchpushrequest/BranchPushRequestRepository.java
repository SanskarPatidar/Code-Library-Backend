package com.sanskar.Code.Library.Backend.repository.branchpushrequest;

import com.sanskar.Code.Library.Backend.model.BranchPushRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchPushRequestRepository extends MongoRepository<BranchPushRequest, String>, BranchPushRequestRepositoryCustom {
    Page<BranchPushRequest> findByRequestedByOrderByRequestedAtDesc(String requestedBy, Pageable pageable);
}
