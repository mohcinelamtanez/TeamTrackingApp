package com.team.tasktracker.completion;

import java.time.LocalDate;
import java.util.List;

import com.team.tasktracker.assignment.TaskType;

/** An official assignment together with the days on which it was completed. */
public record AssignmentStatusDto(
        Long id,
        Long agentId,
        String agentName,
        TaskType taskType,
        LocalDate weekStart,
        List<CompletionDto> completions) {
}
