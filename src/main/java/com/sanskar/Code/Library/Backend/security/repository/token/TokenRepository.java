package com.sanskar.Code.Library.Backend.security.repository.token;

import com.sanskar.Code.Library.Backend.security.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<Token, String>, TokenRepositoryCustom {
    void deleteAllByExpiredTrueAndRevokedTrue();
    Optional<Token> findByToken(String token); // Find token by token(String)
}