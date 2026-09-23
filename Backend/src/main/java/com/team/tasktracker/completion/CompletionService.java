package com.team.tasktracker.completion;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.team.tasktracker.assignment.WeeklyAssignment;
import com.team.tasktracker.assignment.WeeklyAssignmentRepository;
import com.team.tasktracker.common.WeekUtils;

@Service
@Transactional
public class CompletionService {

    private final DailyCompletionRepository completionRepository;
    private final WeeklyAssignmentRepository assignmentRepository;

    public CompletionService(DailyCompletionRepository completionRepository,
            WeeklyAssignmentRepository assignmentRepository) {
        this.completionRepository = completionRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional(readOnly = true)
    public List<AssignmentStatusDto> myWeek(Long agentId, LocalDate weekStart) {
        WeekUtils.requireMonday(weekStart);
        List<WeeklyAssignment> assignments =
                assignmentRepository.findByAgentIdAndWeekStartOrderByTaskTypeAsc(agentId, weekStart);
        return statusFor(assignments, weekStart, WeekUtils.weekEndOf(weekStart));
    }

    /** Pairs each assignment with its completions between the two dates. */
    @Transactional(readOnly = true)
    public List<AssignmentStatusDto> statusFor(List<WeeklyAssignment> assignments, LocalDate from, LocalDate to) {
        if (assignments.isEmpty()) {
            return List.of();
        }
        List<Long> ids = assignments.stream().map(WeeklyAssignment::getId).toList();
        Map<Long, List<CompletionDto>> completionsByAssignment = completionRepository
                .findByAssignmentIdInAndWorkDateBetweenOrderByWorkDateAsc(ids, from, to).stream()
                .collect(Collectors.groupingBy(completion -> completion.getAssignment().getId(),
                        Collectors.mapping(CompletionDto::from, Collectors.toList())));
        return assignments.stream()
                .map(assignment -> new AssignmentStatusDto(
                        assignment.getId(),
                        assignment.getAgent().getId(),
                        assignment.getAgent().getFullName(),
                        assignment.getTaskType(),
                        assignment.getWeekStart(),
                        completionsByAssignment.getOrDefault(assignment.getId(), List.of())))
                .toList();
    }

    /** Agents can only complete their own assignment, and only for today. */
    public CompletionDto markToday(Long agentId, Long assignmentId) {
        WeeklyAssignment assignment = findOwnAssignment(agentId, assignmentId);
        LocalDate today = LocalDate.now();
        if (!assignment.covers(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This assignment is not for the current week");
        }
        DailyCompletion completion = completionRepository.findByAssignmentIdAndWorkDate(assignmentId, today)
                .orElseGet(() -> completionRepository.save(new DailyCompletion(assignment, today)));
        return CompletionDto.from(completion);
    }

    public void undoToday(Long agentId, Long assignmentId) {
        findOwnAssignment(agentId, assignmentId);
        completionRepository.findByAssignmentIdAndWorkDate(assignmentId, LocalDate.now())
                .ifPresent(completionRepository::delete);
    }

    /** Someone else's assignment is reported as "not found" so its existence is not revealed. */
    private WeeklyAssignment findOwnAssignment(Long agentId, Long assignmentId) {
        return assignmentRepository.findByIdAndAgentId(assignmentId, agentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
    }
}
