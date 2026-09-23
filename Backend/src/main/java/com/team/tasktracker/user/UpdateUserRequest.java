package com.team.tasktracker.user;

import jakarta.validation.constraints.Size;

/** Every field is optional: only the provided ones are changed. */
public record UpdateUserRequest(
        @Size(min = 1, max = 100) String fullName,
        Boolean active,
        @Size(min = 6, max = 100) String password) {
}
