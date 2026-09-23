package com.team.tasktracker.completion;

import java.time.Instant;
import java.time.LocalDate;

public record CompletionDto(LocalDate date, Instant completedAt) {

    public static CompletionDto from(DailyCompletion completion) {
        return new CompletionDto(completion.getWorkDate(), completion.getCompletedAt());
    }
}
