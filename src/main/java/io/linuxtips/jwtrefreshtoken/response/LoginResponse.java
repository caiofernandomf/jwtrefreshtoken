package io.linuxtips.jwtrefreshtoken.response;

import lombok.Builder;
import lombok.Getter;

public record LoginResponse(
        String jwtToken,
        String refreshToken,
        String username,
        Long id,
        String type
) {
    public LoginResponse(String jwtToken, String refreshToken, String username, Long id) {
        this(jwtToken, refreshToken, username, id, "bearer");
    }
}
