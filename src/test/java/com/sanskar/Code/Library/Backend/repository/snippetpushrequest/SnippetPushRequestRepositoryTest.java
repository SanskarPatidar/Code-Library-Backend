package com.sanskar.Code.Library.Backend.repository.snippetpushrequest;

import com.sanskar.Code.Library.Backend.model.SnippetPushRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
class SnippetPushRequestRepositoryTest {

    @Autowired
    SnippetPushRequestRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        SnippetPushRequest req1 = SnippetPushRequest.builder()
                .id("1")
                .requesterUsername("user1")
                .requestedAt(LocalDateTime.now().minusMinutes(10))
                .snippetId("s1")
                .build();

        SnippetPushRequest req2 = SnippetPushRequest.builder()
                .id("2")
                .requesterUsername("user1")
                .requestedAt(LocalDateTime.now())
                .snippetId("s2")
                .build();

        SnippetPushRequest req3 = SnippetPushRequest.builder()
                .id("3")
                .requesterUsername("user1")
                .requestedAt(LocalDateTime.now().plusMinutes(10))
                .snippetId("s1")
                .rejected(true)
                .build();

        repository.saveAll(List.of(req1, req2, req3));
    }

    @Test
    void findByRequesterUsernameOrderByRequestedAtDescTest(){
        var page = repository.findByRequesterUsernameOrderByRequestedAtDesc("user1", PageRequest.of(0, 10));
        assertEquals(3, page.getTotalElements());
        List<LocalDateTime> timeList = page.getContent()
                .stream()
                .map(SnippetPushRequest::getRequestedAt)
                .toList();
        assertThat(timeList).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void findBySnippetIdValidTest(){
        var page = repository.findBySnippetIdValid("s1", PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
    }
}