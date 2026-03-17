package com.whatcanicook.service;

import com.whatcanicook.dto.model.FriendDto;
import com.whatcanicook.dto.request.FriendRequest;
import com.whatcanicook.mapper.FriendMapper;
import com.whatcanicook.model.Friend;
import com.whatcanicook.model.User;
import com.whatcanicook.model.enums.FriendStatus;
import com.whatcanicook.repository.FriendRepository;
import com.whatcanicook.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final FriendMapper friendMapper;

    public FriendService(FriendRepository friendRepository,
                         UserRepository userRepository,
                         FriendMapper friendMapper) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.friendMapper = friendMapper;
    }

    public FriendDto sendFriendRequest(FriendRequest request) {
        Long requesterId = request.getRequesterId();
        Long receiverId = request.getReceiverId();

        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("No puedes enviarte una solicitud a ti mismo");
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario emisor no encontrado"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario receptor no encontrado"));

        boolean alreadyExists = friendRepository
                .findByRequesterIdAndReceiverId(requesterId, receiverId)
                .isPresent()
                || friendRepository.findByRequesterIdAndReceiverId(receiverId, requesterId).isPresent();

        if (alreadyExists) {
            throw new IllegalArgumentException("Ya existe una solicitud o amistad entre estos usuarios");
        }

        Friend friendRequest = Friend.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return friendMapper.toDto(friendRepository.save(friendRequest));
    }

    public List<FriendDto> getPendingRequests(Long userId) {
        return friendRepository.findByReceiverIdAndStatus(userId, FriendStatus.PENDING)
                .stream()
                .map(friendMapper::toDto)
                .toList();
    }

    public FriendDto acceptRequest(Long friendId) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        friend.setStatus(FriendStatus.ACCEPTED);
        return friendMapper.toDto(friendRepository.save(friend));
    }

    public FriendDto rejectRequest(Long friendId) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        friend.setStatus(FriendStatus.REJECTED);
        return friendMapper.toDto(friendRepository.save(friend));
    }
}
