package com.sanskar.Code.Library.Backend.service.user;

import com.sanskar.Code.Library.Backend.dto.snippet.PrivateSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.SnippetCreateRequestDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.SnippetUpdateRequestDTO;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.model.Snippet;
import com.sanskar.Code.Library.Backend.repository.snippet.SnippetRepository;
import com.sanskar.Code.Library.Backend.security.repository.UserRepository;
import com.sanskar.Code.Library.Backend.testutil.CsvLoader;
import com.sanskar.Code.Library.Backend.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnippetServiceTest {

    @Mock
    private SnippetRepository snippetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Utils utils;

    @InjectMocks
    private SnippetService snippetService;

    private List<Snippet> testSnippets;

    @BeforeEach
    void setup() {
        testSnippets = CsvLoader.loadSnippets("src/test/resources/Snippet.csv");
    }

    @Nested
    class CreateSnippetTest{

        private SnippetCreateRequestDTO createDto;

        @BeforeEach
        void setup() {
            createDto = SnippetCreateRequestDTO.builder()
                    .title("Test Snippet")
                    .description("Desc")
                    .tags(List.of("java"))
                    .publicVisibility(true)
                    .build();
        }

        @Test
        void allGood() {
            when(utils.getAuthenticatedUsername()).thenReturn("testUser1");
            doNothing().when(utils).saveVersionHistory(any(Snippet.class));
            when(snippetRepository.save(any(Snippet.class))).thenAnswer(it -> it.getArgument(0));

            PrivateSnippetResponseDTO response = snippetService.createSnippet(createDto);

            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo(createDto.getTitle());
            verify(utils).saveVersionHistory(any(Snippet.class));
            verify(snippetRepository).save(any(Snippet.class));
        }
    }

    @Nested
    class UpdateSnippetDirectlyTest {

        private SnippetUpdateRequestDTO updateDto;

        @BeforeEach
        void setup() {
            updateDto = SnippetUpdateRequestDTO.builder()
                    .title("Title2")
                    .build();
        }

        @ParameterizedTest
        @CsvSource(value = {
            "1,testUser0",
            "3,testUser1"
        })
        void allGood(String id, String authorName) {
            // Arrange
            Snippet existingSnippet = testSnippets.get(Integer.parseInt(id));
            updateDto.setSnippetId(id);
            when(utils.getAuthenticatedUsername()).thenReturn(authorName);
            when(snippetRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(existingSnippet));
            when(snippetRepository.save(any(Snippet.class))).thenAnswer(i -> i.getArgument(0));
            doNothing().when(utils).saveVersionHistory(any());

            // Act
            PrivateSnippetResponseDTO response = snippetService.updateSnippetDirectly(updateDto);

            // Assert
            assertThat(response).isNotNull();
            assertThat(existingSnippet.getTitle()).isEqualTo("Title2");

            verify(snippetRepository).save(any());
            verify(utils).saveVersionHistory(any());
        }

        @Test
        void snippetNotFound_throwsException() {
            // Arrange
            updateDto.setSnippetId("1457");
            when(utils.getAuthenticatedUsername()).thenReturn("testUser1");
            when(snippetRepository.findByIdAndDeletedFalse("1457")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> snippetService.updateSnippetDirectly(updateDto))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Snippet not found.");

            verify(snippetRepository, never()).save(any());
            verify(utils, never()).saveVersionHistory(any());
        }

        @ParameterizedTest
        @CsvSource(value = {
            "2,testUser1",
            "3,testUser0"
        })
        void unauthorizedUser_throwsException(String id, String authorName) {
            // Arrange
            Snippet existingSnippet = testSnippets.get(Integer.parseInt(id));
            updateDto.setSnippetId(id);
            when(utils.getAuthenticatedUsername()).thenReturn("testUser");
            when(snippetRepository.findByIdAndDeletedFalse(id)).thenReturn(Optional.of(existingSnippet));

            // Act & Assert
            assertThatThrownBy(() -> snippetService.updateSnippetDirectly(updateDto))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("Only the author can directly update the snippet.");

            verify(snippetRepository, never()).save(any());
            verify(utils, never()).saveVersionHistory(any());
        }
    }

    @Nested
    class GetSnippetTest {

        @ParameterizedTest
        @CsvSource(value = {
            "0,testUser0,1",
            "1,testUser1,2",
        })
        void allGood(String userId, String userName, String snippetId) {
            // Arrange
            Snippet existingSnippet = testSnippets.get(Integer.parseInt(snippetId));
            existingSnippet.setAuthorName(userName);
            when(utils.getAuthenticatedUsername()).thenReturn(userName);
            when(snippetRepository.findByIdAndDeletedFalse(snippetId)).thenReturn(Optional.of(existingSnippet));
            when(utils.getAuthenticatedUserId()).thenReturn(userId);
            // Act
            PrivateSnippetResponseDTO response = snippetService.getSnippetById(snippetId);

            // Assert
            assertThat(response).isNotNull();
        }

        @Test
        void snippetNotFound_throwsException() {
            when(utils.getAuthenticatedUsername()).thenReturn("Me");
            when(snippetRepository.findByIdAndDeletedFalse("999")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> snippetService.getSnippetById("999"))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Snippet not found.");

            verify(utils).getAuthenticatedUsername();
            verify(utils, never()).getAuthenticatedUserId();
        }

        @Test
        void unauthorizedUser_throwsException() {
            Snippet existingSnippet = testSnippets.get(0);
            when(snippetRepository.findByIdAndDeletedFalse("0")).thenReturn(Optional.of(existingSnippet));
            when(utils.getAuthenticatedUsername()).thenReturn("unknownUser");
            when(utils.getAuthenticatedUserId()).thenReturn("69");

            assertThatThrownBy(() -> snippetService.getSnippetById("0"))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("You do not have permission to view this snippet.");
        }
    }

    @Test
    void getCollaboratingSnippetsTest() {
        when(utils.getAuthenticatedUserId()).thenReturn("1");
        when(snippetRepository.findByCollaboratorIdAndDeletedFalse(anyString(), any(Pageable.class))) // don't combine matchers with raw values
                .thenReturn(new PageImpl<>(List.of(testSnippets.get(1), testSnippets.get(2))));

        var response = snippetService.getCollaboratingSnippets(PageRequest.of(0, 10));

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(2);
    }

}