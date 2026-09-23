package com.team.tasktracker.help;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.team.tasktracker.assignment.TaskType;

/** The date and timestamp are set by the server. */
public record CreateHelpRequest(@NotNull TaskType taskType, @Size(max = 255) String note) {
}
