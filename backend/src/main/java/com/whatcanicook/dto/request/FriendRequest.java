package com.whatcanicook.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {

    @NotNull
    private Long requesterId;

    @NotNull
    private Long receiverId;
}
