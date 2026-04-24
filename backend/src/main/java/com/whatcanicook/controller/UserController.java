package com.whatcanicook.controller;

import com.whatcanicook.dto.model.UserDto;
import com.whatcanicook.service.UploadService;
import com.whatcanicook.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UploadService uploadService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(required = false) String username) {
        return ResponseEntity.ok(userService.searchByUsername(username));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping(value = "/{userId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> uploadProfileImage(
            @PathVariable Long userId,
            @RequestPart("file") MultipartFile file
    ) {
        String url = uploadService.storeImage(file, "profiles");
        return ResponseEntity.ok(userService.updateProfileImageUrl(userId, url));
    }
}
