package io.linuxtips.jwtrefreshtoken.repository;

import io.linuxtips.jwtrefreshtoken.model.RefreshToken;
import io.linuxtips.jwtrefreshtoken.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteTokenByUser(User user);
}
