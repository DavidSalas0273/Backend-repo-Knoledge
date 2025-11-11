package com.knoledge.backend.dto;

import com.knoledge.backend.models.Usuario;

public class UsuarioDto {

    private final Long id;
    private final String name;
    private final String email;
    private final String role;
    private final String profesion;
    private final String pais;
    private final String ciudad;
    private final String descripcion;

    private UsuarioDto(Long id, String name, String email, String role,
            String profesion, String pais, String ciudad, String descripcion) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.profesion = profesion;
        this.pais = pais;
        this.ciudad = ciudad;
        this.descripcion = descripcion;
    }

    public static UsuarioDto fromEntity(Usuario usuario) {
        return new UsuarioDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getProfesion(),
                usuario.getPais(),
                usuario.getCiudad(),
                usuario.getDescripcion()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getProfesion() {
        return profesion;
    }

    public String getPais() {
        return pais;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
