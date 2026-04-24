package com.whatcanicook.service;

import com.whatcanicook.dto.model.UserDto;
import com.whatcanicook.mapper.UserMapper;
import com.whatcanicook.model.User;
import com.whatcanicook.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return userMapper.toDto(user);
    }

    public List<UserDto> searchByUsername(String username) {
        if (username == null || username.isBlank()) {
            return getAllUsers();
        }

        return userRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto updateProfileImageUrl(Long userId, String profileImageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setProfileImageUrl(profileImageUrl);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
