package com.whatcanicook.dto.model;

import com.whatcanicook.model.enums.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendDto {

    private Long id;

    private Long requesterId;
    private String requesterUsername;

    private Long receiverId;
    private String receiverUsername;

    private FriendStatus status;

    private LocalDateTime createdAt;
}
