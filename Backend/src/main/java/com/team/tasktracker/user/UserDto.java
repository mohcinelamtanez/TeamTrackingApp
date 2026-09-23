package com.team.tasktracker.user;

public record UserDto(Long id, String username, String fullName, Role role, boolean active) {

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getFullName(), user.getRole(), user.isActive());
    }
}
