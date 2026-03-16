package com.whatcanicook.model;

import com.whatcanicook.model.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que envía la solicitud
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    // Usuario que recibe la solicitud
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { // Así no tenemso que asignar fecha en el service
        createdAt = LocalDateTime.now();
    }
}
