package com.whatcanicook.dto.response;

import com.whatcanicook.dto.model.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private boolean success;
    private String message;
    private UserDto user;

}
