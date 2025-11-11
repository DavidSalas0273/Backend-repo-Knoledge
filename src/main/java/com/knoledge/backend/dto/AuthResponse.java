package com.knoledge.backend.dto;

public class AuthResponse {

    private final String message;
    private final String token;
    private final UsuarioDto user;

    public AuthResponse(String message, String token, UsuarioDto user) {
        this.message = message;
        this.token = token;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public UsuarioDto getUser() {
        return user;
    }
}
