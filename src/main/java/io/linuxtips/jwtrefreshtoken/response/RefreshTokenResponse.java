package io.linuxtips.jwtrefreshtoken.response;

public record RefreshTokenResponse(
        String acessToken,
        String refreshToken,
        String tokenType
) {
}
