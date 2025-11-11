package com.knoledge.backend.services;

import com.knoledge.backend.dto.AuthResponse;
import com.knoledge.backend.dto.LoginRequest;
import com.knoledge.backend.dto.RegistroRequest;
import com.knoledge.backend.dto.UsuarioDto;
import com.knoledge.backend.models.Usuario;
import com.knoledge.backend.repositories.UsuarioRepository;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setProfesion(request.getProfesion());
        usuario.setPais(request.getPais());
        usuario.setCiudad(request.getCiudad());
        usuario.setDescripcion(request.getDescripcion());

        String role = request.getRole();
        if (role != null && !role.isBlank()) {
            usuario.setRole(role.trim().toLowerCase(Locale.ROOT));
        } else {
            usuario.setRole("student");
        }

        Usuario saved = usuarioRepository.save(usuario);
        String token = jwtService.generateToken(saved);
        return new AuthResponse("Usuario registrado correctamente", token, UsuarioDto.fromEntity(saved));
    }

    public AuthResponse login(LoginRequest request) {
        return usuarioRepository.findByEmail(request.getEmail())
                .map(usuario -> {
                    boolean passwordValid = passwordEncoder.matches(request.getPassword(), usuario.getPassword());

                    if (!passwordValid && !usuario.getPassword().startsWith("$2")) {
                        passwordValid = usuario.getPassword().equals(request.getPassword());
                        if (passwordValid) {
                            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
                            usuarioRepository.save(usuario);
                        }
                    }

                    if (!passwordValid) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
                    }

                    String token = jwtService.generateToken(usuario);
                    return new AuthResponse("Inicio de sesión exitoso", token, UsuarioDto.fromEntity(usuario));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
