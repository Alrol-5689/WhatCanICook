package com.whatcanicook.repository;

import com.whatcanicook.model.Friend;
import com.whatcanicook.model.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    // Solicitudes recibidas
    List<Friend> findByReceiverIdAndStatus(Long reciverId, FriendStatus status);

    // Solicitudes enviadas
    List<Friend> findByRequesterIdAndStatus(Long requesterId, FriendStatus status);

    // comprobar si ya existe una relación entre dos usuarios
    Optional<Friend> findByRequesterIdAndReceiverId(Long requesterId, Long receiverId);
}