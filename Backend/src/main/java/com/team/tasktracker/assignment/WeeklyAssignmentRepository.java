package com.team.tasktracker.assignment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyAssignmentRepository extends JpaRepository<WeeklyAssignment, Long> {

    @EntityGraph(attributePaths = "agent")
    List<WeeklyAssignment> findByWeekStartOrderByTaskTypeAscAgentFullNameAsc(LocalDate weekStart);

    @EntityGraph(attributePaths = "agent")
    List<WeeklyAssignment> findByAgentIdAndWeekStartOrderByTaskTypeAsc(Long agentId, LocalDate weekStart);

    Optional<WeeklyAssignment> findByIdAndAgentId(Long id, Long agentId);

    boolean existsByAgentIdAndWeekStartAndTaskType(Long agentId, LocalDate weekStart, TaskType taskType);
}
