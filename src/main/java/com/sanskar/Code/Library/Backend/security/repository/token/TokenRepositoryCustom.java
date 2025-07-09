package com.sanskar.Code.Library.Backend.security.repository.token;

import com.sanskar.Code.Library.Backend.security.model.Token;

import java.util.List;

public interface TokenRepositoryCustom {
    List<Token> findValidTokensByUserIdAndDeviceId(String userId, String deviceId);
}