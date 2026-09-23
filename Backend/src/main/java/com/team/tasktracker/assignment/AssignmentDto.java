package com.team.tasktracker.assignment;

import java.time.LocalDate;

public record AssignmentDto(Long id, Long agentId, String agentName, TaskType taskType, LocalDate weekStart) {

    public static AssignmentDto from(WeeklyAssignment assignment) {
        return new AssignmentDto(assignment.getId(), assignment.getAgent().getId(),
                assignment.getAgent().getFullName(), assignment.getTaskType(), assignment.getWeekStart());
    }
}
