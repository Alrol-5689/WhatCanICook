package com.whatcanicook.controller;

import com.whatcanicook.dto.model.FriendDto;
import com.whatcanicook.dto.request.FriendRequest;
import com.whatcanicook.dto.response.ApiMessageResponse;
import com.whatcanicook.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<FriendDto> sendFriendRequest(@RequestBody FriendRequest request) {
        return ResponseEntity.ok(friendService.sendFriendRequest(request));
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<FriendDto>> getPendingRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(friendService.getPendingRequests(userId));
    }

    @GetMapping("/accepted/{userId}")
    public ResponseEntity<List<FriendDto>> getAcceptedFriends(@PathVariable Long userId) {
        return ResponseEntity.ok(friendService.getAcceptedFriends(userId));
    }

    @PatchMapping("/{friendId}/accept")
    public ResponseEntity<FriendDto> acceptRequest(@PathVariable Long friendId) {
        return ResponseEntity.ok(friendService.acceptRequest(friendId));
    }

    @PatchMapping("/{friendId}/reject")
    public ResponseEntity<FriendDto> rejectRequest(@PathVariable Long friendId) {
        return ResponseEntity.ok(friendService.rejectRequest(friendId));
    }

    @DeleteMapping
    public ResponseEntity<ApiMessageResponse> removeFriendship(@RequestParam Long userId,
                                                               @RequestParam Long friendUserId) {
        friendService.removeFriendship(userId, friendUserId);
        return ResponseEntity.ok(new ApiMessageResponse(true, "Amistad eliminada"));
    }
}
