package com.sanskar.Code.Library.Backend.repository.snippetpushrequest;

import com.sanskar.Code.Library.Backend.model.SnippetPushRequest;
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

@Repository
public class SnippetPushRequestRepositoryCustomImpl implements SnippetPushRequestRepositoryCustom{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<SnippetPushRequest> findBySnippetIdValid(String snippetId, Pageable pageable) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("snippetId").is(snippetId),
                Criteria.where("approved").is(false),
                Criteria.where("rejected").is(false)
        );

        Query query = new Query(criteria)
                .with(pageable)
                .with(Sort.by(Sort.Direction.DESC, "requestedAt"));

        List<SnippetPushRequest> content = mongoTemplate.find(query, SnippetPushRequest.class);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), SnippetPushRequest.class);
        return new PageImpl<>(content, pageable, count);
    }
}
