package com.team.tasktracker.assignment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.team.tasktracker.common.WeekUtils;
import com.team.tasktracker.completion.DailyCompletionRepository;
import com.team.tasktracker.user.Role;
import com.team.tasktracker.user.User;
import com.team.tasktracker.user.UserRepository;

@Service
@Transactional
public class AssignmentService {

    private final WeeklyAssignmentRepository assignmentRepository;
    private final DailyCompletionRepository completionRepository;
    private final UserRepository userRepository;

    public AssignmentService(WeeklyAssignmentRepository assignmentRepository,
            DailyCompletionRepository completionRepository, UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.completionRepository = completionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AssignmentDto> listWeek(LocalDate weekStart) {
        WeekUtils.requireMonday(weekStart);
        return assignmentRepository.findByWeekStartOrderByTaskTypeAscAgentFullNameAsc(weekStart).stream()
                .map(AssignmentDto::from)
                .toList();
    }

    public AssignmentDto create(CreateAssignmentRequest request, Long supportId) {
        WeekUtils.requireMonday(request.weekStart());
        User agent = userRepository.findById(request.agentId())
                .filter(user -> user.getRole() == Role.AGENT && user.isActive())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown or inactive agent"));
        if (assignmentRepository.existsByAgentIdAndWeekStartAndTaskType(agent.getId(), request.weekStart(),
                request.taskType())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This agent is already assigned to this task type for this week");
        }
        User support = userRepository.getReferenceById(supportId);
        return AssignmentDto.from(assignmentRepository.save(
                new WeeklyAssignment(agent, request.taskType(), request.weekStart(), support)));
    }

    /** Removal is only allowed while the assignment has no completion history. */
    public void delete(Long id) {
        WeeklyAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        if (completionRepository.existsByAssignmentId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This assignment already has completed days and cannot be removed");
        }
        assignmentRepository.delete(assignment);
    }

    /** Copies one week's assignments into another week, skipping inactive agents and existing ones. */
    public List<AssignmentDto> copyWeek(LocalDate from, LocalDate to, Long supportId) {
        WeekUtils.requireMonday(from);
        WeekUtils.requireMonday(to);
        User support = userRepository.getReferenceById(supportId);
        List<AssignmentDto> created = new ArrayList<>();
        for (WeeklyAssignment source : assignmentRepository.findByWeekStartOrderByTaskTypeAscAgentFullNameAsc(from)) {
            User agent = source.getAgent();
            if (!agent.isActive()
                    || assignmentRepository.existsByAgentIdAndWeekStartAndTaskType(agent.getId(), to,
                            source.getTaskType())) {
                continue;
            }
            WeeklyAssignment copy = new WeeklyAssignment(agent, source.getTaskType(), to, support);
            created.add(AssignmentDto.from(assignmentRepository.save(copy)));
        }
        return created;
    }
}
