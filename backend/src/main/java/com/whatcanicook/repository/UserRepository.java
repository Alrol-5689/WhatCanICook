package com.whatcanicook.repository;

import com.whatcanicook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /*User user = userRepository.findByEmail(email);
    if (user != null) {
        // usuario encontrado
    } else {
        // usuario no encontrado
    }*/
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Búsqueda por username parcial, ignore case
    List<User> findByUsernameContainingIgnoreCase(String username);




}
