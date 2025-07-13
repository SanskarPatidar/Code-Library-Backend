package com.sanskar.Code.Library.Backend.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanskar.Code.Library.Backend.dto.snippet.PrivateSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.SnippetCreateRequestDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.SnippetUpdateRequestDTO;
import com.sanskar.Code.Library.Backend.model.Snippet;
import com.sanskar.Code.Library.Backend.service.user.SnippetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.in;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(SnippetController.class)
class SnippetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SnippetService snippetService;

    private SnippetCreateRequestDTO createDto;
    private SnippetUpdateRequestDTO updateDto;
    private PrivateSnippetResponseDTO responseDto;

    @BeforeEach
    void setUp() {
        createDto = SnippetCreateRequestDTO.builder()
                .title("Title")
                .description("Description")
                .tags(List.of("java", "spring"))
                .language("java")
                .code("System.out.println('Hello');")
                .publicVisibility(true)
                .allowPublicDownload(true)
                .build();

        updateDto = SnippetUpdateRequestDTO.builder()
                .snippetId("123")
                .title("Updated Title")
                .build();

        responseDto = new PrivateSnippetResponseDTO(
                Snippet.builder().id("123").title("Title").build()
        );
    }

    @Test
    void createSnippet() throws Exception {
        when(snippetService.createSnippet(any(SnippetCreateRequestDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/snippet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.title").value("Title"));


    }

    @Test
    void updateSnippet() {
    }

    @Test
    void getSnippetById() {
    }

    @Test
    void getMySnippets() {
    }

    @Test
    void getCollaboratingSnippets() {
    }

    @Test
    void deleteSnippet() {
    }

    @Test
    void pullSnippet() {
    }

    @Test
    void togglePublicVisibility() {
    }

    @Test
    void getPublicSnippets() {
    }
}