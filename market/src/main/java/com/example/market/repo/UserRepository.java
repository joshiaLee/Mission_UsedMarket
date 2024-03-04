package com.example.market.repo;

import com.example.market.entity.UserEntity;
import com.example.market.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    List<UserEntity> findByAuthoritiesAndStatus(String authorities, Status status);


}
