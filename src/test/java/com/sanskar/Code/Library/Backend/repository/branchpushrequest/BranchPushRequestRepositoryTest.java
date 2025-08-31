package com.sanskar.Code.Library.Backend.repository.branchpushrequest;

import com.sanskar.Code.Library.Backend.model.BranchPushRequest;
import com.sanskar.Code.Library.Backend.model.BranchPushRequestStatus;
import com.sanskar.Code.Library.Backend.repository.MongoTestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class BranchPushRequestRepositoryTest extends MongoTestContainer {

    @Autowired
    BranchPushRequestRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        // Arrange
        List<BranchPushRequest> requests = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BranchPushRequest request = BranchPushRequest.builder()
                    .id(String.valueOf(i))
                    .snippetId("s0")
                    .targetBranchId("b0")
                    .requestedBy("r0")
                    .requestedAt(LocalDateTime.now().plusMinutes(i * 10)) // 10, 20, 30... minutes
                    .status(i % 2 == 0 ? BranchPushRequestStatus.PENDING : BranchPushRequestStatus.REJECTED)
                    .build();

            requests.add(request);
        }
        repository.saveAll(requests);
    }

    @Test
    void findAllBySnippetIdAndValidAsPage() {
        // Act
        var page = repository.findAllBySnippetIdAndValidAsPage("s0", PageRequest.of(0, 10));

        // Assert
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getContent())
                .extracting(BranchPushRequest::getRequestedAt)
                .isSortedAccordingTo(Comparator.reverseOrder());

        assertThat(page.getContent())
                .extracting(BranchPushRequest::getStatus)
                .containsOnly(BranchPushRequestStatus.PENDING);
    }

    @Test
    void findByRequestedByOrderByRequestedAtDesc() {
        var result = repository.findByRequestedByOrderByRequestedAtDesc("r0", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(10);

        assertThat(result.getContent())
                .extracting(BranchPushRequest::getRequestedBy)
                .isSortedAccordingTo(Comparator.reverseOrder());

    }
}