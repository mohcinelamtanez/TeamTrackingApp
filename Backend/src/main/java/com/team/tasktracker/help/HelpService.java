package com.team.tasktracker.help;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.team.tasktracker.assignment.WeeklyAssignmentRepository;
import com.team.tasktracker.common.WeekUtils;
import com.team.tasktracker.user.UserRepository;

@Service
@Transactional
public class HelpService {

    private final HelpRecordRepository helpRepository;
    private final WeeklyAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public HelpService(HelpRecordRepository helpRepository, WeeklyAssignmentRepository assignmentRepository,
            UserRepository userRepository) {
        this.helpRepository = helpRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<HelpDto> myHelp(Long agentId, LocalDate from, LocalDate to) {
        return helpRepository.findByAgentIdAndHelpDateBetweenOrderByHelpDateDescCreatedAtDesc(agentId, from, to)
                .stream()
                .map(HelpDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HelpDto> teamHelp(LocalDate from, LocalDate to) {
        return helpRepository.findByHelpDateBetweenOrderByHelpDateAscCreatedAtAsc(from, to).stream()
                .map(HelpDto::from)
                .toList();
    }

    /** Records help for today. It never touches the agent's official assignments. */
    public HelpDto create(Long agentId, CreateHelpRequest request) {
        LocalDate today = LocalDate.now();
        if (assignmentRepository.existsByAgentIdAndWeekStartAndTaskType(agentId, WeekUtils.weekStartOf(today),
                request.taskType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You are officially assigned to this task this week. Mark it as completed instead.");
        }
        if (helpRepository.existsByAgentIdAndTaskTypeAndHelpDate(agentId, request.taskType(), today)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Help with this task is already recorded today");
        }
        String note = request.note() == null || request.note().isBlank() ? null : request.note().trim();
        HelpRecord help = new HelpRecord(userRepository.getReferenceById(agentId), request.taskType(), today, note);
        return HelpDto.from(helpRepository.save(help));
    }

    /** Agents may remove their own help record, on the same day only. */
    public void delete(Long agentId, Long helpId) {
        HelpRecord help = helpRepository.findByIdAndAgentId(helpId, agentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Help record not found"));
        if (!help.getHelpDate().equals(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only today's help records can be removed");
        }
        helpRepository.delete(help);
    }
}
