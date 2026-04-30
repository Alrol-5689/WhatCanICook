package com.whatcanicook.service;

import com.whatcanicook.dto.model.IngredientDto;
import com.whatcanicook.dto.model.UserDto;
import com.whatcanicook.mapper.IngredientMapper;
import com.whatcanicook.mapper.UserMapper;
import com.whatcanicook.model.Ingredient;
import com.whatcanicook.model.User;
import com.whatcanicook.repository.IngredientRepository;
import com.whatcanicook.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper,
                       IngredientRepository ingredientRepository, IngredientMapper ingredientMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
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

    public List<IngredientDto> getPantry(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return user.getPantryIngredients().stream()
                .map(ingredientMapper::toDto)
                .toList();
    }

    public List<IngredientDto> addToPantry(Long userId, Long ingredientId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente no encontrado"));

        if (!user.getPantryIngredients().contains(ingredient)) {
            user.getPantryIngredients().add(ingredient);
            userRepository.save(user);
        }

        return getPantry(userId);
    }

    public List<IngredientDto> removeFromPantry(Long userId, Long ingredientId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente no encontrado"));

        if (user.getPantryIngredients().contains(ingredient)) {
            user.getPantryIngredients().remove(ingredient);
            userRepository.save(user);
        }

        return getPantry(userId);
    }
}
