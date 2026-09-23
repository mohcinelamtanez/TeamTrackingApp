package com.team.tasktracker.report;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.tasktracker.assignment.WeeklyAssignment;
import com.team.tasktracker.assignment.WeeklyAssignmentRepository;
import com.team.tasktracker.common.WeekUtils;
import com.team.tasktracker.completion.CompletionService;
import com.team.tasktracker.help.HelpService;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final WeeklyAssignmentRepository assignmentRepository;
    private final CompletionService completionService;
    private final HelpService helpService;

    public ReportService(WeeklyAssignmentRepository assignmentRepository, CompletionService completionService,
            HelpService helpService) {
        this.assignmentRepository = assignmentRepository;
        this.completionService = completionService;
        this.helpService = helpService;
    }

    public ReportDto daily(LocalDate date) {
        return build(WeekUtils.weekStartOf(date), date, date);
    }

    public ReportDto weekly(LocalDate weekStart) {
        WeekUtils.requireMonday(weekStart);
        return build(weekStart, weekStart, WeekUtils.weekEndOf(weekStart));
    }

    private ReportDto build(LocalDate weekStart, LocalDate from, LocalDate to) {
        List<WeeklyAssignment> assignments =
                assignmentRepository.findByWeekStartOrderByTaskTypeAscAgentFullNameAsc(weekStart);
        return new ReportDto(from, to,
                completionService.statusFor(assignments, from, to),
                helpService.teamHelp(from, to));
    }
}
