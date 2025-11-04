    package com.knoledge.backend.repository;

    import com.knoledge.backend.models.Usuario;
    import org.springframework.data.jpa.repository.JpaRepository;

    public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
        boolean existsByEmail(String email);
    }