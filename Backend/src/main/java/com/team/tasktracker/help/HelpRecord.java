package com.team.tasktracker.help;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.team.tasktracker.assignment.TaskType;
import com.team.tasktracker.user.User;

/**
 * ADDITIONAL help: "this agent voluntarily helped with this task type on this date".
 * Deliberately not linked to WeeklyAssignment, so recording help can never change an official assignment.
 */
@Entity
@Table(name = "help_records",
        uniqueConstraints = @UniqueConstraint(columnNames = {"agent_id", "task_type", "help_date"}))
public class HelpRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_id")
    private User agent;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 20)
    private TaskType taskType;

    @Column(name = "help_date", nullable = false)
    private LocalDate helpDate;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(length = 255)
    private String note;

    protected HelpRecord() {
    }

    public HelpRecord(User agent, TaskType taskType, LocalDate helpDate, String note) {
        this.agent = agent;
        this.taskType = taskType;
        this.helpDate = helpDate;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public User getAgent() {
        return agent;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public LocalDate getHelpDate() {
        return helpDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getNote() {
        return note;
    }
}
