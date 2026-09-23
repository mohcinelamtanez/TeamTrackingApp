package com.team.tasktracker.help;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.team.tasktracker.assignment.TaskType;

public interface HelpRecordRepository extends JpaRepository<HelpRecord, Long> {

    boolean existsByAgentIdAndTaskTypeAndHelpDate(Long agentId, TaskType taskType, LocalDate helpDate);

    Optional<HelpRecord> findByIdAndAgentId(Long id, Long agentId);

    @EntityGraph(attributePaths = "agent")
    List<HelpRecord> findByAgentIdAndHelpDateBetweenOrderByHelpDateDescCreatedAtDesc(Long agentId, LocalDate from,
            LocalDate to);

    @EntityGraph(attributePaths = "agent")
    List<HelpRecord> findByHelpDateBetweenOrderByHelpDateAscCreatedAtAsc(LocalDate from, LocalDate to);
}
