package com.team.tasktracker.assignment;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record CreateAssignmentRequest(@NotNull Long agentId, @NotNull TaskType taskType, @NotNull LocalDate weekStart) {
}
