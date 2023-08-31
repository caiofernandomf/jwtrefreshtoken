package io.linuxtips.jwtrefreshtoken.request;

import java.io.Serializable;

public record LoginRequest(
        String username,
        String password) implements Serializable {
}
