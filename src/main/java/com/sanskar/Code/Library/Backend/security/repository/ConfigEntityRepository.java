package com.sanskar.Code.Library.Backend.security.repository;

import com.sanskar.Code.Library.Backend.security.model.ConfigEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigEntityRepository extends MongoRepository<ConfigEntity, String> {
}
