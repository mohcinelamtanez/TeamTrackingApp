package com.team.tasktracker.completion;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.team.tasktracker.assignment.WeeklyAssignment;

/** "The agent finished this official assignment on this date." The row existing means completed. */
@Entity
@Table(name = "daily_completions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"assignment_id", "work_date"}))
public class DailyCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id")
    private WeeklyAssignment assignment;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(nullable = false, updatable = false)
    private Instant completedAt = Instant.now();

    protected DailyCompletion() {
    }

    public DailyCompletion(WeeklyAssignment assignment, LocalDate workDate) {
        this.assignment = assignment;
        this.workDate = workDate;
    }

    public Long getId() {
        return id;
    }

    public WeeklyAssignment getAssignment() {
        return assignment;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
