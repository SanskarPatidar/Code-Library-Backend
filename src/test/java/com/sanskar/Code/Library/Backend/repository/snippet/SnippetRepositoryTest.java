package com.sanskar.Code.Library.Backend.repository.snippet;

import com.sanskar.Code.Library.Backend.model.Snippet;
import com.sanskar.Code.Library.Backend.repository.MongoTestContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class SnippetRepositoryTest extends MongoTestContainer {

    @Autowired
    private SnippetRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        List<Snippet> snippets = new ArrayList<>();
        for(int i = 0; i<10; i++) {
            Snippet snippet1 = Snippet.builder()
                    .id(i + "")
                    .publicVisibility(false)
                    .tags(List.of("java", "mongo"))
                    .authorName(i % 2 == 0 ? "u0" : "u1")
                    .collaborators(Map.of(i % 2 == 0 ? "1" : "0", i % 2 == 0 ? "u1" : "u0"))
                    .deleted(i == 0 || i == 1 || i == 6)
                    .build();
            snippets.add(snippet1);
        }
        repository.saveAll(snippets);
    }


    @ParameterizedTest
    @CsvSource({
            "'0', 4",
            "'1', 3"
    })
    void findByCollaboratorIdAndDeletedFalse(String collaboratorId, int expected) {
        var result = repository.findByCollaboratorIdAndDeletedFalse(collaboratorId, PageRequest.of(0, 10));
        System.out.println(result.getContent());
        assertThat(result.getTotalElements()).isEqualTo(expected);

        result.getContent().forEach(snippet -> {
            assertThat(snippet.isDeleted()).isFalse();
            assertThat(snippet.getCollaborators()).containsKey(collaboratorId);
        });
    }
}