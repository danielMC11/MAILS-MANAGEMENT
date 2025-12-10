package com.project.repository;


import com.project.entity.RefreshToken;
import com.project.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> deleteByUser(Usuario user);
    Optional<RefreshToken> findByUser(Usuario user);
}