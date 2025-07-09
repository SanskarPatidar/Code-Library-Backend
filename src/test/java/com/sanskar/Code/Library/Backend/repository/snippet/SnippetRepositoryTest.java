package com.sanskar.Code.Library.Backend.repository.snippet;

import com.sanskar.Code.Library.Backend.model.Snippet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
class SnippetRepositoryTest {

    @Autowired
    private SnippetRepository snippetRepository;

    @BeforeEach
    public void setUp() {
        snippetRepository.deleteAll();

        Snippet snippet1 = Snippet.builder()
                .id("1")
                .publicVisibility(true)
                .tags(List.of("java", "mongo"))
                .authorName("user1")
                .collaborators(Map.of("user3", "user3"))
                .build();
        Snippet snippet2 = Snippet.builder()
                .id("2")
                .publicVisibility(false)
                .tags(List.of("mongo"))
                .authorName("user1")
                .collaborators(Map.of("user1", "user1"))
                .build();
        Snippet snippet3 = Snippet.builder()
                .id("3")
                .deleted(true)
                .publicVisibility(true)
                .tags(List.of("java"))
                .authorName("user2")
                .collaborators(Map.of("user2", "user2", "user1", "user1"))
                .build();

        snippetRepository.saveAll(List.of(snippet1, snippet2, snippet3));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "1,true",
        "3,false",
        "2,true"
    })
    void findByIdAndDeletedFalseTest(String id, boolean expected) {
        Optional<Snippet> actual = snippetRepository.findByIdAndDeletedFalse(id);
        assertEquals(expected, actual.isPresent());
    }

    @Test
    void findByPublicVisibilityTrueAndDeletedFalseTest() {
        var page = snippetRepository.findByPublicVisibilityTrueAndDeletedFalse(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "'mongo', 1",
        "'mongo,java', 1",
        "'', 0",
        "'nonexistent', 0"
    })
    void findByPublicVisibilityTrueAndDeletedFalseAndTagsInTest(String tagList, int expected) {
        List<String> tags = tagList.isEmpty()? List.of() : List.of(tagList.split(","));
        var page = snippetRepository.findByPublicVisibilityTrueAndDeletedFalseAndTagsIn(tags, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "user1, 2",
        "user2, 0",
        "user3, 0"
    })
    void findAllByAuthorNameAndDeletedFalseTest(String userName, int expected) {
        var page = snippetRepository.findAllByAuthorNameAndDeletedFalse(userName, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "user1, 1",
            "user2, 0",
            "user3, 1"
    })
    void findByCollaboratorIdAndDeletedFalseTest(String collaboratorId, int expected) {
        var page = snippetRepository.findByCollaboratorIdAndDeletedFalse(collaboratorId, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(expected);
    }
}