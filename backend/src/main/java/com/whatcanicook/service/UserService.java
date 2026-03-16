package com.whatcanicook.service;

import com.whatcanicook.dto.model.UserDto;
import com.whatcanicook.model.User;
import com.whatcanicook.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return mapToDto(user);
    }

    public List<UserDto> searchByUsername(String username) {
        if (username == null || username.isBlank()) {
            return getAllUsers();
        }

        return userRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
