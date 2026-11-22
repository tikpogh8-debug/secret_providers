package com.dev.secretproviders.controller;

import com.dev.secretproviders.service.SecretNotFoundException;
import com.dev.secretproviders.service.SecretService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequestMapping("/secrets")
public class SecretController {

    private static final String LINK_ATTRIBUTE = "link";

    private final SecretService secretService;

    public SecretController(SecretService secretService) {
        this.secretService = secretService;
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("secretForm", new SecretForm());
        return "secret-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute SecretForm secretForm,
                         BindingResult bindingResult,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "secret-form";
        }

        String token = secretService.store(secretForm.getContent(), authentication.getName());
        redirectAttributes.addFlashAttribute(LINK_ATTRIBUTE, buildLink(token));
        return "redirect:/secrets/created";
    }

    @GetMapping("/created")
    public String showLink(Model model) {
        if (!model.containsAttribute(LINK_ATTRIBUTE)) {
            return "redirect:/secrets/new";
        }
        return "secret-link";
    }

    /**
     * Shows a confirmation page instead of the secret itself, so link previews
     * and crawlers that issue GET requests cannot consume the secret.
     */
    @GetMapping("/{token}")
    public String confirmReveal(@PathVariable String token, Model model) {
        if (!secretService.exists(token)) {
            throw new SecretNotFoundException();
        }
        model.addAttribute("token", token);
        return "secret-confirm";
    }

    @PostMapping("/{token}/reveal")
    public String reveal(@PathVariable String token, Model model) {
        model.addAttribute("content", secretService.reveal(token));
        return "secret-view";
    }

    private String buildLink(String token) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/secrets/{token}")
                .buildAndExpand(token)
                .toUriString();
    }

}
