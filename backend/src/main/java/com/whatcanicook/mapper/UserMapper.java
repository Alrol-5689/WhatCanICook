package com.whatcanicook.mapper;

import com.whatcanicook.dto.model.UserDto;
import com.whatcanicook.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
