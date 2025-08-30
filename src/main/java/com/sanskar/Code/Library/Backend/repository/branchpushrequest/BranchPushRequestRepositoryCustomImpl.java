package com.sanskar.Code.Library.Backend.repository.branchpushrequest;

import com.sanskar.Code.Library.Backend.model.BranchPushRequest;
import com.sanskar.Code.Library.Backend.model.BranchPushRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BranchPushRequestRepositoryCustomImpl implements BranchPushRequestRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<BranchPushRequest> findAllBySnippetIdAndValidAsPage(String snippetId, Pageable pageable) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("snippetId").is(snippetId),
                Criteria.where("status").is(BranchPushRequestStatus.PENDING)
        );

        Query query = new Query(criteria)
                .with(pageable)
                .with(Sort.by(Sort.Direction.DESC, "requestedAt"));

        List<BranchPushRequest> content = mongoTemplate.find(query, BranchPushRequest.class);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), BranchPushRequest.class);
        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public Optional<BranchPushRequest> findByIdValid(String id) {
        Criteria criteria = Criteria.where("id").is(id)
                .and("status").is(BranchPushRequestStatus.PENDING);
        Query query = new Query(criteria);
        return Optional.ofNullable(mongoTemplate.findOne(query, BranchPushRequest.class));
    }

    @Override
    public List<BranchPushRequest> findAllValidBySnippetId(String snippetId) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("snippetId").is(snippetId),
                Criteria.where("status").is(BranchPushRequestStatus.PENDING)
        );

        Query query = new Query(criteria);
        return mongoTemplate.find(query, BranchPushRequest.class);
    }
}
