package com.sanskar.Code.Library.Backend.repository.snippet;

import com.sanskar.Code.Library.Backend.model.Snippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SnippetRepositoryCustomImpl implements SnippetRepositoryCustom{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<Snippet> findByCollaboratorIdAndDeletedFalse(String userId, Pageable pageable) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("deleted").is(false),
                Criteria.where("collaborators." + userId).exists(true) // go inside collaborators map using '.' and check for userId
        );

        Query query = new Query(criteria).with(pageable);
        List<Snippet> snippets = mongoTemplate.find(query, Snippet.class);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Snippet.class);

        return new PageImpl<>(snippets, pageable, count); // Page is interface, return PageImpl which takes list of snippets, making it Page of snippets
    }

}
