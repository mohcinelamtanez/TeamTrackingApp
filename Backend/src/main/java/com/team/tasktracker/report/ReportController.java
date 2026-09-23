package com.team.tasktracker.report;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.team.tasktracker.help.HelpDto;
import com.team.tasktracker.help.HelpService;

/** Read-only team views for SUPPORT (see SecurityConfig). */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final HelpService helpService;

    public ReportController(ReportService reportService, HelpService helpService) {
        this.reportService = reportService;
        this.helpService = helpService;
    }

    /** Defaults to today. */
    @GetMapping("/daily")
    public ReportDto daily(@RequestParam(required = false) LocalDate date) {
        return reportService.daily(date != null ? date : LocalDate.now());
    }

    @GetMapping("/weekly")
    public ReportDto weekly(@RequestParam LocalDate weekStart) {
        return reportService.weekly(weekStart);
    }

    @GetMapping("/help")
    public List<HelpDto> help(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        if (to.isBefore(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'to' must not be before 'from'");
        }
        return helpService.teamHelp(from, to);
    }
}
