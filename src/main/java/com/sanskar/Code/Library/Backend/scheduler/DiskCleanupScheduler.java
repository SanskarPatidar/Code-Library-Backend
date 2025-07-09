package com.sanskar.Code.Library.Backend.scheduler;

import com.sanskar.Code.Library.Backend.security.repository.token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DiskCleanupScheduler {

    @Autowired
    private TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 0 1 * ?") // Runs at midnight on the first day of every month
    public void jwtTokenCleanup(){
        tokenRepository.deleteAllByExpiredTrueAndRevokedTrue();
    }
}
