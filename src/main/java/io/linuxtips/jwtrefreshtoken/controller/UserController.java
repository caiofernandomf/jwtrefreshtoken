package io.linuxtips.jwtrefreshtoken.controller;

import io.linuxtips.jwtrefreshtoken.jwt.Util;
import io.linuxtips.jwtrefreshtoken.model.RefreshToken;
import io.linuxtips.jwtrefreshtoken.model.User;
import io.linuxtips.jwtrefreshtoken.request.LoginRequest;
import io.linuxtips.jwtrefreshtoken.request.RefreshTokenRequest;
import io.linuxtips.jwtrefreshtoken.response.RefreshTokenResponse;
import io.linuxtips.jwtrefreshtoken.service.RefreshTokenService;
import io.linuxtips.jwtrefreshtoken.service.UserService;
import io.linuxtips.jwtrefreshtoken.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {

    private final UserService userService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;
    private final Util util;

    public UserController(UserService userService, LoginService loginService, RefreshTokenService refreshTokenService, Util util) {
        this.userService = userService;
        this.loginService = loginService;
        this.refreshTokenService = refreshTokenService;
        this.util = util;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user){
        log.info("criando um novo usuário com as infos : [{}]", user);
        return this.userService.save(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login (@RequestBody LoginRequest loginRequest) throws Exception {
        log.info("usuário [{}] fazendo login", loginRequest.username());
        return  loginService.login(loginRequest);
    }

    @PostMapping("/refreshtoken")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> refreshtoken( @RequestBody RefreshTokenRequest request) {

        log.info("Solicitação de criação de refresh token para o token [{}]", request);
        return
                refreshTokenService.findByToken(request.refreshToken())
                        .map(refreshTokenService::verifyExpiration)
                        .map(RefreshToken::getUser)
                        .map(user -> {
                            String token = refreshTokenService.generateTokenFromUserName(user.getUsername());
                            log.info("refresh token gerado com sucesso [{}]", token);
                            return ResponseEntity.ok(new RefreshTokenResponse(token, request.refreshToken(), "Bearer"));
                        }).orElseThrow(() -> new CredentialsExpiredException(request.refreshToken()));

    }

    @GetMapping("/test-login")
    public String testuser(){
        return "logado";
    }
}
