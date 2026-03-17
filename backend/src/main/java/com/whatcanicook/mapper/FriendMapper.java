package com.whatcanicook.mapper;

import com.whatcanicook.dto.model.FriendDto;
import com.whatcanicook.model.Friend;
import org.springframework.stereotype.Component;

@Component
public class FriendMapper {

    public FriendDto toDto(Friend friend) {
        return new FriendDto(
                friend.getId(),
                friend.getRequester().getId(),
                friend.getRequester().getUsername(),
                friend.getReceiver().getId(),
                friend.getReceiver().getUsername(),
                friend.getStatus(),
                friend.getCreatedAt()
        );
    }
}
