package com.knoledge.backend.services;

import com.knoledge.backend.models.Usuario;
import com.knoledge.backend.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public CurrentUserService(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario requireUser(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.toLowerCase().startsWith("bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }
        String token = authorizationHeader.substring(7);
        Long userId;
        try {
            userId = jwtService.getUserId(token);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        return usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }
}
