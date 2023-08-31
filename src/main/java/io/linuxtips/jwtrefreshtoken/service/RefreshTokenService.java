package io.linuxtips.jwtrefreshtoken.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import io.linuxtips.jwtrefreshtoken.model.RefreshToken;
import io.linuxtips.jwtrefreshtoken.repository.RefreshTokenRepository;
import io.linuxtips.jwtrefreshtoken.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenService {

    private final String SECRET_KEY = "linuxtips";

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId){

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpirationTime(LocalDateTime.now().plus(1000 * 60 *2, ChronoUnit.MILLIS));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken=refreshTokenRepository.save(refreshToken);

        return refreshToken;

    }

    public String generateTokenFromUserName(String username){

        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 *2))
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64URL.encode(SECRET_KEY))
                .compact();

    }

    public RefreshToken verifyExpiration(RefreshToken token){

        if(token.getExpirationTime().compareTo(LocalDateTime.now()) < 0){
            refreshTokenRepository.delete(token);

            log.error("refresh token expirado em [{}], efetue login novamente", token.getExpirationTime());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return token;
    }

    @Transactional
    public int deleteUserById(Long userId){
        return refreshTokenRepository.deleteTokenByUser(userRepository.findById(userId).get());
    }
}
