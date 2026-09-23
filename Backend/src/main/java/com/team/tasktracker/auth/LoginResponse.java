package com.team.tasktracker.auth;

import com.team.tasktracker.user.UserDto;

public record LoginResponse(String token, UserDto user) {
}
