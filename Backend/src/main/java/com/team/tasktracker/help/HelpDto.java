package com.team.tasktracker.help;

import java.time.Instant;
import java.time.LocalDate;

import com.team.tasktracker.assignment.TaskType;

public record HelpDto(Long id, Long agentId, String agentName, TaskType taskType, LocalDate date, Instant createdAt,
        String note) {

    public static HelpDto from(HelpRecord help) {
        return new HelpDto(help.getId(), help.getAgent().getId(), help.getAgent().getFullName(), help.getTaskType(),
                help.getHelpDate(), help.getCreatedAt(), help.getNote());
    }
}
