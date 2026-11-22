package com.dev.secretproviders.controller;

import com.dev.secretproviders.service.SecretService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecretControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecretService secretService;

    @Test
    void anonymousUserIsRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/secrets/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(authorities = "OTHER")
    void userWithoutStandardPermissionIsForbidden() throws Exception {
        mockMvc.perform(get("/secrets/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "STANDARD")
    void standardUserCreatesSecretAndGetsLink() throws Exception {
        mockMvc.perform(post("/secrets").param("content", "my secret").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/secrets/created"))
                .andExpect(flash().attributeExists("link"));
    }

    @Test
    @WithMockUser(authorities = "STANDARD")
    void blankSecretIsRejected() throws Exception {
        mockMvc.perform(post("/secrets").param("content", "  ").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("secret-form"));
    }

    @Test
    @WithMockUser(authorities = "STANDARD")
    void openingLinkDoesNotConsumeSecret() throws Exception {
        String token = secretService.store("my secret", "sender@example.com");

        mockMvc.perform(get("/secrets/" + token))
                .andExpect(status().isOk())
                .andExpect(view().name("secret-confirm"));

        assertThat(secretService.exists(token)).isTrue();
    }

    @Test
    @WithMockUser(authorities = "STANDARD")
    void secretCanBeRevealedOnlyOnce() throws Exception {
        String token = secretService.store("my secret", "sender@example.com");

        mockMvc.perform(post("/secrets/" + token + "/reveal").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("my secret")));

        mockMvc.perform(post("/secrets/" + token + "/reveal").with(csrf()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/secrets/" + token))
                .andExpect(status().isNotFound());
    }

}
