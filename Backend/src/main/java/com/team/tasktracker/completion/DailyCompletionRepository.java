package com.team.tasktracker.completion;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCompletionRepository extends JpaRepository<DailyCompletion, Long> {

    boolean existsByAssignmentId(Long assignmentId);

    Optional<DailyCompletion> findByAssignmentIdAndWorkDate(Long assignmentId, LocalDate workDate);

    List<DailyCompletion> findByAssignmentIdInAndWorkDateBetweenOrderByWorkDateAsc(Collection<Long> assignmentIds,
            LocalDate from, LocalDate to);
}
