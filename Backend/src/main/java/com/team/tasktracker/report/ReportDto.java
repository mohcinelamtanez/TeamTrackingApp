package com.team.tasktracker.report;

import java.time.LocalDate;
import java.util.List;

import com.team.tasktracker.completion.AssignmentStatusDto;
import com.team.tasktracker.help.HelpDto;

/**
 * Official work and additional help are reported in separate lists.
 * For a daily report {@code from == to}; for a weekly report they span Monday to Sunday.
 */
public record ReportDto(LocalDate from, LocalDate to, List<AssignmentStatusDto> assignments, List<HelpDto> help) {
}
