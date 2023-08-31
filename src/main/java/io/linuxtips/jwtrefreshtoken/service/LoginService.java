package io.linuxtips.jwtrefreshtoken.service;

import io.linuxtips.jwtrefreshtoken.jwt.Util;
import io.linuxtips.jwtrefreshtoken.model.RefreshToken;
import io.linuxtips.jwtrefreshtoken.request.LoginRequest;
import io.linuxtips.jwtrefreshtoken.response.LoginResponse;
import io.linuxtips.jwtrefreshtoken.security.AppUser;
import io.linuxtips.jwtrefreshtoken.security.SecurityConfig;
import io.linuxtips.jwtrefreshtoken.security.UserAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final Util util;
    private final UserAuthenticationService userAuthenticationService;
    private final SecurityConfig securityConfig;
    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;

    public LoginService(Util util, UserAuthenticationService userAuthenticationService, SecurityConfig securityConfig, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager) {
        this.util = util;
        this.userAuthenticationService = userAuthenticationService;
        this.securityConfig = securityConfig;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<?> login(LoginRequest loginRequest)throws Exception{
        try{

            /*Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),loginRequest.password()
                    ));*/

            final UserDetails userDetails = userAuthenticationService.loadUserByUsername(loginRequest.username());

            AppUser appUser = (AppUser) userDetails;

            if(!securityConfig.passwordEncoder().matches(loginRequest.password(), appUser.getPassword())) {
                throw new BadCredentialsException("Credenciais inválidas");
            }

            final String jwtToken =  util.generateToken(userDetails);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(appUser.getUser().getId());

            return ResponseEntity.ok(new LoginResponse(
                    jwtToken,  refreshToken.getToken(), appUser.getUsername(),
                    appUser.getUser().getId()
            ));

        }catch (BadCredentialsException e){
            throw new Exception("Usuário e senha incorretas", e);
        }
    }
}
