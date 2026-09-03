package com.kaamsetu.modules.user.repository;

import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import com.kaamsetu.modules.user.entity.enums.UserStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    
    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    Optional<UserEntity> findByMobile(String mobile);

    Optional<UserEntity> findByEmailIgnoreCase(String email);
    
    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByMobile(String mobile);

    boolean existsByEmailIgnoreCase(String email);
    
    List<UserEntity> findByStatus(UserStatusEnum status);

    List<UserEntity> findByRole(RoleEnum role);

    long countByStatus(UserStatusEnum status);

    long countByRole(RoleEnum role);
}
