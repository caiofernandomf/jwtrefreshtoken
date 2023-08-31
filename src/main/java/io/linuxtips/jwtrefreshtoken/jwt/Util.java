package io.linuxtips.jwtrefreshtoken.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class Util {

    private final String SECRET_KEY = "linuxtips";

    public String getUsername(String token){
        return getClaim(token,Claims::getSubject);
    }

    public LocalDateTime verifyExpriration(String token){
        return LocalDateTime.ofInstant(
                getClaim(token,Claims::getExpiration).toInstant(),
                ZoneId.systemDefault()
        );
    }

    public <T> T getClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token){
        return Jwts.parser().setSigningKey(TextCodec.BASE64URL.encode(SECRET_KEY)).parseClaimsJws(token).getBody();
    }

    private Boolean verifyTokenExpired(String token){
        return verifyExpriration(token).isBefore(LocalDateTime.now());
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims,userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject){

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60))
                .signWith(SignatureAlgorithm.HS256,TextCodec.BASE64URL.encode(SECRET_KEY)).compact();
    }

    public Boolean verifyToken(String token, UserDetails userDetails){
        final String username= getUsername(token);
        return username.equals(userDetails.getUsername()) && !verifyTokenExpired(token);
    }
}
