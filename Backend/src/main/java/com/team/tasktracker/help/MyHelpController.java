package com.team.tasktracker.help;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team.tasktracker.auth.CurrentUser;

/** The logged-in agent's additional help records. AGENT only. */
@RestController
@RequestMapping("/api/me/help")
public class MyHelpController {

    private final HelpService helpService;

    public MyHelpController(HelpService helpService) {
        this.helpService = helpService;
    }

    /** Defaults to today. */
    @GetMapping
    public List<HelpDto> myHelp(@RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to, @AuthenticationPrincipal Jwt jwt) {
        LocalDate today = LocalDate.now();
        return helpService.myHelp(CurrentUser.id(jwt), from != null ? from : today, to != null ? to : today);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HelpDto create(@Valid @RequestBody CreateHelpRequest request, @AuthenticationPrincipal Jwt jwt) {
        return helpService.create(CurrentUser.id(jwt), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        helpService.delete(CurrentUser.id(jwt), id);
    }
}
