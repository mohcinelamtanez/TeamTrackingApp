package com.team.tasktracker.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserDto> list(Role role) {
        List<User> users = role == null
                ? userRepository.findAllByOrderByFullNameAsc()
                : userRepository.findByRoleOrderByFullNameAsc(role);
        return users.stream().map(UserDto::from).toList();
    }

    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        return UserDto.from(find(id));
    }

    public UserDto create(CreateUserRequest request) {
        String username = request.username().trim();
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        User user = new User(username, passwordEncoder.encode(request.password()), request.fullName().trim(),
                request.role());
        return UserDto.from(userRepository.save(user));
    }

    public UserDto update(Long id, UpdateUserRequest request, Long currentUserId) {
        User user = find(id);
        if (request.fullName() != null) {
            user.setFullName(request.fullName().trim());
        }
        if (request.active() != null) {
            if (!request.active() && id.equals(currentUserId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot deactivate your own account");
            }
            user.setActive(request.active());
        }
        if (request.password() != null) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return UserDto.from(user);
    }

    private User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
