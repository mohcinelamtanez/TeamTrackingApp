package com.team.tasktracker.assignment;

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

import com.team.tasktracker.user.User;

/**
 * OFFICIAL weekly assignment: "this agent is responsible for this task type during this week".
 * Only SUPPORT creates or removes these. There is no limit on how many agents share a task type.
 */
@Entity
@Table(name = "weekly_assignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"agent_id", "week_start", "task_type"}))
public class WeeklyAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_id")
    private User agent;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 20)
    private TaskType taskType;

    /** Always a Monday. */
    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected WeeklyAssignment() {
    }

    public WeeklyAssignment(User agent, TaskType taskType, LocalDate weekStart, User createdBy) {
        this.agent = agent;
        this.taskType = taskType;
        this.weekStart = weekStart;
        this.createdBy = createdBy;
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

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean covers(LocalDate date) {
        return !date.isBefore(weekStart) && !date.isAfter(weekStart.plusDays(6));
    }
}
