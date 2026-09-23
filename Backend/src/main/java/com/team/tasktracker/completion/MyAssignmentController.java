package com.team.tasktracker.completion;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team.tasktracker.auth.CurrentUser;
import com.team.tasktracker.common.WeekUtils;

/** The logged-in agent's official assignments (read-only) and their daily completion. AGENT only. */
@RestController
@RequestMapping("/api/me/assignments")
public class MyAssignmentController {

    private final CompletionService completionService;

    public MyAssignmentController(CompletionService completionService) {
        this.completionService = completionService;
    }

    /** Defaults to the current week. */
    @GetMapping
    public List<AssignmentStatusDto> myWeek(@RequestParam(required = false) LocalDate weekStart,
            @AuthenticationPrincipal Jwt jwt) {
        LocalDate week = weekStart != null ? weekStart : WeekUtils.weekStartOf(LocalDate.now());
        return completionService.myWeek(CurrentUser.id(jwt), week);
    }

    @PutMapping("/{assignmentId}/completions/today")
    public CompletionDto markToday(@PathVariable Long assignmentId, @AuthenticationPrincipal Jwt jwt) {
        return completionService.markToday(CurrentUser.id(jwt), assignmentId);
    }

    @DeleteMapping("/{assignmentId}/completions/today")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void undoToday(@PathVariable Long assignmentId, @AuthenticationPrincipal Jwt jwt) {
        completionService.undoToday(CurrentUser.id(jwt), assignmentId);
    }
}
